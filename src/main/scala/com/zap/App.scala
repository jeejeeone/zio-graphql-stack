package com.zap

import com.zap.database.ClickHouseConnection
import com.zap.graphql.GraphQLApi
import com.zap.graphql.queries.PersonGraphQL
import com.zap.http.HttpApp
import com.zap.observability.metrics.PrometheusMetrics
import com.zap.observability.tracing.Tracer
import com.zap.redis.jedis.{JedisClient, JedisSingleConnection}
import com.zap.zquery.{AddressDataSource, CountryDataSource, PersonDataSource}
import zio.*
import zio.http.*
import zio.telemetry.opentelemetry.context.ContextStorage
import zio.telemetry.opentelemetry.tracing.Tracing

object App extends ZIOAppDefault:
  override def run =
    (for
      _       <- ZIO.logInfo("Start")
      httpApp <- HttpApp.httpAppZIO
      _       <- Server.serve(httpApp).provideSome[Tracing](Server.default)
    yield ())
      .provide(
        PersonGraphQL.live,
        GraphQLApi.live,
        AddressDataSource.live,
        PersonDataSource.live,
        CountryDataSource.live,
        ClickHouseConnection.live,
        PrometheusMetrics.live,
        JedisSingleConnection.live,
        JedisClient.live,
        Tracer.live,
        Tracing.live,
        ContextStorage.fiberRef,
      )
