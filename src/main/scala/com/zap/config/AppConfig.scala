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
  case class HttpServerConfig(httpPort: Int = 8080)

  val clickHouseConfig: Config[ClickHouseConfig] = deriveConfig[ClickHouseConfig].toSnakeCase
  val httpServerConfig: Config[HttpServerConfig] = deriveConfig[HttpServerConfig].toSnakeCase
