package com.zap.config

import zio.config.*
import zio.Config
import zio.config.magnolia.{deriveConfig, name}

object AppConfig:
  case class ClickHouseConfig(chJdbcUrl: String, chUser: String, chPassword: String)
  case class HttpServerConfig(httpPort: Int)

  val clickHouseConfig: Config[ClickHouseConfig] = deriveConfig[ClickHouseConfig].toSnakeCase
  val serverConfig:     Config[HttpServerConfig] = deriveConfig[HttpServerConfig].toSnakeCase
