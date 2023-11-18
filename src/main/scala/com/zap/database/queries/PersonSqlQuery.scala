package com.zap.database.queries

import anorm.*
import com.zap.database.model.{PersonRow, PersonWithAddressRow}
import com.zap.model.PersonId
import io.github.gaelrenoux.tranzactio.*
import io.github.gaelrenoux.tranzactio.anorm.*

object PersonSqlQuery:
  def getPersonsWithAddress = tzio: c =>
    SQL("SELECT * FROM person_address_tupled").as(PersonWithAddressRow.personWithAddressParser.*)(c)

  def getPersons = tzio: c =>
    SQL("SELECT * FROM person").as(PersonRow.personParser.*)(c)

  def getPersons(id: PersonId) = tzio: c =>
    SQL(s"SELECT * FROM person WHERE id = $id").as(PersonRow.personParser.singleOpt)(c)

  def getPersons(ids: List[PersonId]) = tzio: c =>
    SQL(s"SELECT * FROM person WHERE id in [${ids.mkString(",")}]").as(PersonRow.personParser.*)(c)
