package com.zap.database

import com.clickhouse.jdbc.ClickHouseDataSource
import com.zap.config.AppConfig
import com.zap.config.AppConfig.ClickHouseConfig
import io.github.gaelrenoux.tranzactio.anorm.Database
import zio.{ZIO, ZLayer}

object ClickHouseConnection:
  val live =
    (ZLayer:
      ZIO.config[ClickHouseConfig](AppConfig.clickHouseConfig).map: chConfig =>
        new ClickHouseDataSource(s"${chConfig.chJdbcUrl}?user=${chConfig.chUser}&password=${chConfig.chPassword}")
    ) >>> Database.fromDatasource
