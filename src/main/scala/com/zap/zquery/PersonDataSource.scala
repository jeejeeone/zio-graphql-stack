package com.zap.zquery

import com.zap.database.model.PersonRow
import com.zap.database.queries.PersonSqlQuery.personQuery
import com.zap.model.*
import com.zap.zquery.PersonDataSource.{GetAllPersons, GetPerson}
import io.github.gaelrenoux.tranzactio.anorm.Database
import zio.query.DataSource.Batched
import zio.query.{CompletedRequestMap, DataSource, Request}
import zio.{Exit, ZIO, ZLayer}

trait PersonDataSource:
  def personDataSource:     UDataSource[GetPerson]
  def allPersonsDataSource: UDataSource[GetAllPersons]

object PersonDataSource:
  case class GetPerson(id: PersonId) extends Request[Nothing, Option[PersonRow]]
  case class GetAllPersons()         extends Request[Nothing, List[PersonRow]]

  val live =
    ZLayer:
      for
        database <- ZIO.service[Database.Service]
      yield new PersonDataSource:
        override lazy val personDataSource: UDataSource[GetPerson] =
          Batched.make[Any, GetPerson]("PersonDataSource"): requests =>
            val resultMap = CompletedRequestMap.empty
            requests.toList match
              case request :: Nil =>
                database.autoCommitOrWiden(personQuery(request.id)).exit.map(e => resultMap.insert(request)(e))
              case batch =>
                database.autoCommitOrWiden(personQuery(batch.map(_.id))).fold(
                  err =>
                    requests.foldLeft(resultMap) { case (map, req) =>
                      map.insert(req)(Exit.fail(err))
                    },
                  success =>
                    success.foldLeft(resultMap) { case (map, person) =>
                      map.insert(GetPerson(person.id))(Exit.succeed(Some(person)))
                    },
                )

        override lazy val allPersonsDataSource =
          DataSource.fromFunctionZIO[Any, Throwable, GetAllPersons, List[PersonRow]]("AllPersonsDataSource"): r =>
            database.autoCommitOrWiden(personQuery)
