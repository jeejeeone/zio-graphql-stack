package com.zap.database.service

import com.zap.database.model.Person
import com.zap.database.queries.PersonSqlQuery
import com.zap.model.PersonId
import io.github.gaelrenoux.tranzactio.anorm.Database
import zio.{Task, URLayer, ZIO, ZLayer}

trait PersonService:
  def getPersons(): Task[List[Person]]
  def getPerson(id:  PersonId):       Task[Option[Person]]
  def getPerson(ids: List[PersonId]): Task[List[Person]]

object PersonService:
  val live: URLayer[Database.Service, PersonService] = ZLayer.derive[PersonServiceLive]

case class PersonServiceLive(chDatabase: Database.Service) extends PersonService:
  override def getPersons(): Task[List[Person]] =
    chDatabase.autoCommitOrWiden(PersonSqlQuery.getPersons)

  override def getPerson(id: PersonId): Task[Option[Person]] =
    chDatabase.autoCommitOrWiden(PersonSqlQuery.getPersons(id))

  override def getPerson(ids: List[PersonId]): Task[List[Person]] =
    chDatabase.autoCommitOrWiden(PersonSqlQuery.getPersons(ids))
