package com.zap.database

import anorm.*
import anorm.SqlParser.*
import io.github.gaelrenoux.tranzactio.*
import io.github.gaelrenoux.tranzactio.anorm.*

object PersonSqlQuery:
  val getPersons = tzio: c =>
    SQL("SELECT * FROM person_address_tupled").as(SqlParser.int(1).single)(c)
