package com.zap.observability.metrics

import com.zap.config.AppConfig
import zio.{ZIO, ZLayer}
import zio.metrics.connectors.{MetricsConfig, prometheus}
import zio.metrics.jvm.DefaultJvmMetrics

import java.time.Duration

object MetricsLayers:
  val metricsConfig =
    ZLayer:
      ZIO.config[AppConfig.MetricsConfig](AppConfig.metricsConfig).map: config =>
        MetricsConfig(Duration.ofSeconds(config.metricsPollIntervalSeconds))

  val metricsLayers =
    (metricsConfig ++ prometheus.publisherLayer) >+> prometheus.prometheusLayer ++ DefaultJvmMetrics.live.unit
