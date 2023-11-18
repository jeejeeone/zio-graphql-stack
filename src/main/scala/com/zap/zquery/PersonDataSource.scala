package com.zap.zquery

import com.zap.database.model.PersonRow
import com.zap.database.service.PersonService
import com.zap.model.*
import com.zap.zquery.PersonDataSource.{GetAllPersons, GetPerson}
import zio.query.DataSource.Batched
import zio.query.{CompletedRequestMap, DataSource, Request}
import zio.{Exit, URLayer, ZIO, ZLayer}

trait PersonDataSource:
  def personDataSource:     UDataSource[GetPerson]
  def allPersonsDataSource: UDataSource[GetAllPersons]

object PersonDataSource:
  case class GetPerson(id: PersonId) extends Request[Nothing, Option[PersonRow]]
  case class GetAllPersons()         extends Request[Nothing, List[PersonRow]]

  val live: URLayer[PersonService, PersonDataSource] =
    ZLayer:
      for
        personService <- ZIO.service[PersonService]
      yield new PersonDataSource:
        override lazy val personDataSource: UDataSource[GetPerson] =
          Batched.make[Any, GetPerson]("PersonDataSource"): requests =>
            val resultMap = CompletedRequestMap.empty
            requests.toList match
              case request :: Nil =>
                personService.getPerson(request.id).exit.map(e => resultMap.insert(request)(e))
              case batch =>
                personService.getPerson(batch.map(_.id)).fold(
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
            personService.getPersons()
