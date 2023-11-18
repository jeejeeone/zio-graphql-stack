package com.zap.service

import com.zap.model.PersonId
import com.zap.repositories.PersonData
import com.zap.repositories.PersonData.PersonRow
import zio.{Task, ZIO, ZLayer}

trait PersonService:
  def getPersons(): Task[List[PersonRow]]
  def getPerson(id: PersonId): Task[Option[PersonRow]]
  def getPerson(ids: List[PersonId]): Task[List[PersonRow]]

object PersonService:
  val live = ZLayer.succeed(PersonServiceLive())

case class PersonServiceLive() extends PersonService:
  override def getPersons(): Task[List[PersonRow]] =
    ZIO.succeed(PersonData.persons)
  override def getPerson(id: PersonId): Task[Option[PersonRow]] =
    ZIO.succeed(PersonData.persons.find(p => p.id == id))
  override def getPerson(ids: List[PersonId]): Task[List[PersonRow]] =
    ZIO.succeed(
      PersonData.persons.collect:
        case p if ids.contains(p.id) => p
    )
