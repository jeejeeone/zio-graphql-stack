package com.zap.database.service

import com.zap.database.model.PersonRow
import com.zap.database.queries.PersonSqlQuery
import com.zap.model.PersonId
import io.github.gaelrenoux.tranzactio.anorm.Database
import zio.{Task, URLayer, ZIO, ZLayer}

trait PersonService:
  def getPersons(): Task[List[PersonRow]]
  def getPerson(id:  PersonId):       Task[Option[PersonRow]]
  def getPerson(ids: List[PersonId]): Task[List[PersonRow]]

object PersonService:
  val live: URLayer[Database.Service, PersonService] = ZLayer.derive[PersonServiceLive]

case class PersonServiceLive(chDatabase: Database.Service) extends PersonService:
  override def getPersons(): Task[List[PersonRow]] =
    chDatabase.autoCommitOrWiden(PersonSqlQuery.getPersons)

  override def getPerson(id: PersonId): Task[Option[PersonRow]] =
    chDatabase.autoCommitOrWiden(PersonSqlQuery.getPersons(id))

  override def getPerson(ids: List[PersonId]): Task[List[PersonRow]] =
    chDatabase.autoCommitOrWiden(PersonSqlQuery.getPersons(ids))
