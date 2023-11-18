package com.zap.database

import com.zap.repositories.PersonData.PersonRow
import io.github.gaelrenoux.tranzactio.anorm.{Connection, Database}
import zio.{Task, ZIO, ZLayer}
import com.zap.database.PersonSqlQuery.getPersons
import io.github.gaelrenoux.tranzactio.DbException

trait ClickHouseQuery:
  def persons(): Task[Int]

object ClickHouseQuery:
  val live =
    ZLayer:
      for
        clickHouseConnection <- ZIO.service[Database.Service]
      yield new ClickHouseQuery:
        override def persons(): Task[Int] =
          clickHouseConnection.autoCommitOrWiden(getPersons)
