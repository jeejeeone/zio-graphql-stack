package com.zap.database

import anorm.*
import anorm.SqlParser.*
import io.github.gaelrenoux.tranzactio.*
import io.github.gaelrenoux.tranzactio.anorm.*

object PersonSqlQuery:
  val getPersons = tzio: c =>
    SQL("SELECT -1").as(SqlParser.int(1).single)(c)
