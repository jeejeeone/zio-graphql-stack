package com.zap.http

import caliban.GraphiQLHandler
import caliban.quick.*
import com.zap.database.queries.HealthQuery
import com.zap.graphql.GraphQLApi
import io.github.gaelrenoux.tranzactio.anorm.Database
import zio.*
import zio.http.*
import zio.metrics.connectors.prometheus.PrometheusPublisher

object HttpApp:
  val httpAppZIO =
    for
      graphQLApi          <- ZIO.service[GraphQLApi]
      database            <- ZIO.service[Database.Service]
      prometheusPublisher <- ZIO.service[PrometheusPublisher]
      apiHandlers        <- graphQLApi.api.handlers
    yield
      Routes(
        Method.POST / "api" / "graphql" -> 
          apiHandlers.api,
        Method.GET / "graphiql" -> 
          GraphiQLHandler.handler(apiPath = "/api/graphql", graphiqlPath = "/graphiql"),
        Method.GET / "metrics" -> 
          Handler.fromZIO(prometheusPublisher.get.map(Response.text)),
        Method.GET / "health" / "readiness" -> 
          Handler.ok,
        Method.GET / "health" / "liveness" ->
          Handler.fromZIO:
            database.autoCommitOrWiden(HealthQuery.healthQuery)
              .as(Response.ok)
              .orElseFail(Response.error(Status.InternalServerError)),
      ).toHttpApp
