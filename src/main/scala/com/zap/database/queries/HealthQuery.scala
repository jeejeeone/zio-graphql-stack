package com.zap.database.queries

import anorm.*
import io.github.gaelrenoux.tranzactio.*
import io.github.gaelrenoux.tranzactio.anorm.*

object HealthQuery:
  def healthQuery: TranzactIO[Int] = tzio: c =>
    SQL("SELECT -1").as(SqlParser.int(1).single)(c)
