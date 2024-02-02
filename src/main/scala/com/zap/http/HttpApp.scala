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
  val experiment = Method.POST / "api" / "graphql" -> handler {
    (r: Request) => graphQL.provideSome[GraphQLApi](ZLayer.succeed(Token(1)))
  }.mapError(_ => Response.text("zap"))
    .flatten

  // Could def graphQL(token: Token) ... ? or simply move to experiment
  val graphQL =
    ZIO.serviceWithZIO[GraphQLApi](a => a.api.handlers zip ZIO.service[Token])
      .map(h => h._1.api.provideLayer(ZLayer.succeed(h._2)))

  val httpAppZIO: ZIO[PrometheusPublisher & Database.Service & GraphQLApi, CalibanError.ValidationError, HttpApp[Any]] =
    for
      graphQLApi          <- ZIO.service[GraphQLApi]
      database            <- ZIO.service[Database.Service]
      prometheusPublisher <- ZIO.service[PrometheusPublisher]
      api                 <- ZIO.service[GraphQLApi]
    yield Routes(
      // Could HttpApp[GraphQLApi]
      experiment.provideEnvironment(ZEnvironment.apply(api)),
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
