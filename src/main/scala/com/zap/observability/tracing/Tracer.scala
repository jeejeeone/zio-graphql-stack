package com.zap.observability.tracing

import com.zap.config.AppConfig
import com.zap.config.AppConfig.TracingConfig
import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.context.propagation.ContextPropagators
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter
import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.sdk.resources.Resource
import io.opentelemetry.sdk.trace.SdkTracerProvider
import io.opentelemetry.sdk.trace.`export`.SimpleSpanProcessor
import zio.telemetry.opentelemetry.tracing.propagation.TraceContextPropagator
import zio.{Task, ZIO, ZLayer}

object Tracer:
  val instrumentationScopeName = "com.zap.zio-graphql-stack"

  val live = ZLayer:
    ZIO.config[AppConfig.TracingConfig](AppConfig.tracingConfig)
      .flatMap(make)
      .map(_.getTracer(instrumentationScopeName))

  //val propagator = ZLayer.succeed(TraceContextPropagator.default)

  def make(tracingConfig: TracingConfig): Task[OpenTelemetry] =
    ZIO.attempt:
      OtlpGrpcSpanExporter
        .builder()
        .setEndpoint(tracingConfig.tracingUrl)
        .build()
    .map: exporter =>
      val processor     = SimpleSpanProcessor.create(exporter)
      val traceProvider =
        SdkTracerProvider
          .builder()
          .setResource(Resource.create(Attributes.builder().put("service.name", "zio-graphql-stack").build()))
          .addSpanProcessor(processor)
          .build()

      OpenTelemetrySdk.builder()
        .setTracerProvider(traceProvider)
        .setPropagators(ContextPropagators.create(TraceContextPropagator.default.instance))
        .build()
    .when(tracingConfig.tracingEnabled)
      .map(_.getOrElse(OpenTelemetry.noop()))
