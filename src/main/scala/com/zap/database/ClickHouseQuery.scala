package com.zap.database

import com.zap.database.PersonSqlQuery.{getAddresses, getPersons, getPersonsWithAddress}
import com.zap.database.model.{Address, Person, PersonWithAddress}
import com.zap.model.{AddressId, PersonId}
import io.github.gaelrenoux.tranzactio.DbException
import io.github.gaelrenoux.tranzactio.anorm.{Connection, Database}
import zio.{Task, ZIO, ZLayer}

trait ClickHouseQuery:
  def personsWithAddress(): Task[List[PersonWithAddress]]
  def persons():            Task[List[Person]]
  def addresses():          Task[List[Address]]
  def persons(id:    PersonId):        Task[Option[Person]]
  def addresses(id:  AddressId):       Task[Option[Address]]
  def persons(ids:   List[PersonId]):  Task[List[Person]]
  def addresses(ids: List[AddressId]): Task[List[Address]]

object ClickHouseQuery:
  val live =
    ZLayer:
      for
        clickHouseConnection <- ZIO.service[Database.Service]
      yield new ClickHouseQuery:
        override def persons(): Task[List[Person]] =
          clickHouseConnection.autoCommitOrWiden(getPersons)

        override def persons(id: PersonId): Task[Option[Person]] =
          clickHouseConnection.autoCommitOrWiden(getPersons(id))

        override def persons(ids: List[PersonId]): Task[List[Person]] =
          clickHouseConnection.autoCommitOrWiden(getPersons(ids))

        override def personsWithAddress(): Task[List[PersonWithAddress]] =
          clickHouseConnection.autoCommitOrWiden(getPersonsWithAddress)

        override def addresses(): Task[List[Address]] =
          clickHouseConnection.autoCommitOrWiden(getAddresses)

        override def addresses(id: AddressId): Task[Option[Address]] =
          clickHouseConnection.autoCommitOrWiden(getAddresses(id))

        override def addresses(ids: List[AddressId]): Task[List[Address]] =
          clickHouseConnection.autoCommitOrWiden(getAddresses(ids))
