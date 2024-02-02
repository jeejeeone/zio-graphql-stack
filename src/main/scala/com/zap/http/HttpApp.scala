package com.zap.http

import caliban.{CalibanError, GraphiQLHandler}
import caliban.quick.*
import com.zap.database.queries.HealthQuery
import com.zap.graphql.GraphQLApi
import com.zap.model.Token
import io.github.gaelrenoux.tranzactio.anorm.Database
import zio.*
import zio.http.*
import zio.metrics.connectors.prometheus.PrometheusPublisher

object HttpApp:
  // We can wrap graphql handlers with something to access request, ie decode token and provide to graphql
  val experiment = Method.POST / "api" / "graphql" -> handler {
    (r: Request) => Console.printLine(s"R: ${r.url}") *> graphQL.provideSomeLayer[GraphQLApi](ZLayer.succeed(Token(1)))
  }.mapError(_ => Response.text("zap"))
    .flatten

  val graphQL =
    for
      graphQLApi <- ZIO.service[GraphQLApi]
      token      <- ZIO.service[Token]
      handlers   <- graphQLApi.api.handlers
    yield handlers.api.provideLayer(ZLayer.succeed(token))

  val httpAppZIO
      : ZIO[PrometheusPublisher & Database.Service & GraphQLApi, CalibanError.ValidationError, HttpApp[GraphQLApi]] =
    for
      graphQLApi          <- ZIO.service[GraphQLApi]
      database            <- ZIO.service[Database.Service]
      prometheusPublisher <- ZIO.service[PrometheusPublisher]
      api                 <- ZIO.service[GraphQLApi]
    yield Routes(
      experiment,
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
