package com.zap.database

import anorm.*
import com.zap.database.parser.PersonWithAddress
import io.github.gaelrenoux.tranzactio.*
import io.github.gaelrenoux.tranzactio.anorm.*

object PersonSqlQuery:
  val getPersons = tzio: c =>
    SQL("SELECT * FROM person_address_tupled").as(PersonWithAddress.personWithAddressParser.*)(c)
