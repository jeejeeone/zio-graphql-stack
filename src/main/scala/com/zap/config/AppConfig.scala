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

  case class MetricsConfig(
      metricsPollIntervalSeconds: Int = 5
    )

  case class RedisConfig(
      redisUrl: String = "http://localhost:6379"
    )

  val clickHouseConfig: Config[ClickHouseConfig] = deriveConfig[ClickHouseConfig].toSnakeCase
  val metricsConfig:    Config[MetricsConfig]    = deriveConfig[MetricsConfig].toSnakeCase
  val redisConfig:      Config[RedisConfig]      = deriveConfig[RedisConfig].toSnakeCase
