package com.zap.database.queries

import anorm.*
import com.zap.database.model.{PersonRow, PersonWithAddressRow}
import com.zap.model.PersonId
import io.github.gaelrenoux.tranzactio.*
import io.github.gaelrenoux.tranzactio.anorm.*

object PersonSqlQuery:
  def personWithAddressQuery = tzio: c =>
    SQL("SELECT * FROM person_address_tupled").as(PersonWithAddressRow.personWithAddressParser.*)(c)

  def personQuery = tzio: c =>
    SQL("SELECT * FROM person").as(PersonRow.personParser.*)(c)

  def personQuery(id: PersonId) = tzio: c =>
    SQL(s"SELECT * FROM person WHERE id = $id").as(PersonRow.personParser.singleOpt)(c)

  def personQuery(ids: List[PersonId]) = tzio: c =>
    SQL(s"SELECT * FROM person WHERE id in [${ids.mkString(",")}]").as(PersonRow.personParser.*)(c)
