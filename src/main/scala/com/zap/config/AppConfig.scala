package com.zap.config

import zio.config.*
import zio.Config
import zio.config.magnolia.{deriveConfig, name}

object AppConfig:
  case class ClickHouseConfig(
      chJdbcUrl:  String = "jdbc:clickhouse://localhost:8123/test",
      chUser:     String = "test",
      chPassword: String = "test",
    )

  val clickHouseConfig: Config[ClickHouseConfig] = deriveConfig[ClickHouseConfig].toSnakeCase
