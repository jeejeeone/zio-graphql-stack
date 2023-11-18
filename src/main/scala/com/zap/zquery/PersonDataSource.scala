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
                val result = personService.getPerson(request.id)
                result.exit.map(e => resultMap.insert(request)(e))
              case batch =>
                val result = personService.getPerson(batch.map(_.id))

                val res = result.fold(
                  err =>
                    requests.foldLeft(resultMap) { case (map, req) =>
                      map.insert(req)(Exit.fail(err))
                    },
                  success =>
                    success.foldLeft(resultMap) { case (map, person) =>
                      map.insert(GetPerson(person.id))(Exit.succeed(Some(person)))
                    },
                )

                res

        override lazy val allPersonsDataSource =
          DataSource.fromFunctionZIO[Any, Throwable, GetAllPersons, List[PersonRow]]("AllPersonsDataSource"): r =>
            personService.getPersons()
