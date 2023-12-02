package com.zap.observability.metrics

import com.zap.config.AppConfig
import zio.{ZIO, ZLayer}
import zio.metrics.connectors.{MetricsConfig, prometheus}
import zio.metrics.jvm.DefaultJvmMetrics

import java.time.Duration

object PrometheusMetrics:
  private val metricsConfig =
    ZLayer:
      ZIO.config[AppConfig.MetricsConfig](AppConfig.metricsConfig).map: config =>
        MetricsConfig(Duration.ofSeconds(config.metricsPollIntervalSeconds))

  val live =
    (metricsConfig ++ prometheus.publisherLayer) >+> prometheus.prometheusLayer ++ DefaultJvmMetrics.live.unit
