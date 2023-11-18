package com.zap.database.queries

import anorm.*
import com.zap.database.model.{Address, Person, PersonWithAddress}
import com.zap.model.{AddressId, PersonId}
import io.github.gaelrenoux.tranzactio.*
import io.github.gaelrenoux.tranzactio.anorm.*

object PersonSqlQuery:
  def getPersonsWithAddress = tzio: c =>
    SQL("SELECT * FROM person_address_tupled").as(PersonWithAddress.personWithAddressParser.*)(c)

  def getPersons = tzio: c =>
    SQL("SELECT * FROM person").as(Person.personParser.*)(c)

  def getPersons(id: PersonId) = tzio: c =>
    SQL(s"SELECT * FROM person WHERE id = $id").as(Person.personParser.singleOpt)(c)

  def getPersons(ids: List[PersonId]) = tzio: c =>
    SQL(s"SELECT * FROM person WHERE id in [${ids.mkString(",")}]").as(Person.personParser.*)(c)

  def getAddresses = tzio: c =>
    SQL("SELECT * FROM address").as(Address.addressParser.*)(c)

  def getAddresses(ids: List[AddressId]) = tzio: c =>
    SQL(s"SELECT * FROM address WHERE id in [${ids.mkString(",")}]").as(Address.addressParser.*)(c)

  def getAddresses(id: AddressId) = tzio: c =>
    SQL(s"SELECT * FROM address WHERE id = $id").as(Address.addressParser.singleOpt)(c)
