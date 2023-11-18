package com.zap.service

import com.zap.database.ClickHouseQuery
import com.zap.database.model.Person
import com.zap.model.PersonId
import zio.{Task, URLayer, ZIO, ZLayer}

trait PersonService:
  def getPersons(): Task[List[Person]]
  def getPerson(id:  PersonId):       Task[Option[Person]]
  def getPerson(ids: List[PersonId]): Task[List[Person]]

object PersonService:
  val live: URLayer[ClickHouseQuery, PersonService] = ZLayer.derive[PersonServiceLive]

case class PersonServiceLive(clickHouseQuery: ClickHouseQuery) extends PersonService:
  override def getPersons(): Task[List[Person]] =
    clickHouseQuery.persons()
  override def getPerson(id: PersonId): Task[Option[Person]] =
    clickHouseQuery.persons(id)
  override def getPerson(ids: List[PersonId]): Task[List[Person]] =
    clickHouseQuery.persons(ids)
