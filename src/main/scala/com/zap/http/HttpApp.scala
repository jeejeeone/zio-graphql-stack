package com.zap.http

import caliban.GraphiQLHandler
import caliban.quick.*
import com.zap.database.queries.HealthQuery
import com.zap.graphql.GraphQLApi
import io.github.gaelrenoux.tranzactio.anorm.Database
import zio.*
import zio.http.*

object HttpApp:
  val httpAppZIO =
    for
      graphQLApi <- ZIO.service[GraphQLApi]
      database   <- ZIO.service[Database.Service]
      apiHandler <- graphQLApi.api.handler
    yield Http.collectHandler[Request]:
      case _ -> Root / "api" / "graphql" =>
        apiHandler
      case _ -> Root / "graphiql" =>
        GraphiQLHandler.handler(apiPath = "/api/graphql", graphiqlPath = "/graphiql")
      case _ -> Root / "metrics" =>
        Handler.fromFunction(_ => Response.text("metrics"))
      case _ -> Root / "health" / "liveness" =>
        val livenessZIO =
          database.autoCommitOrWiden(HealthQuery.healthQuery)
            .as(Response.ok)
            .orElseFail(Response.fromHttpError(HttpError.InternalServerError()))

        Handler.fromZIO(livenessZIO)
      case _ -> Root / "health" / "readiness" => Handler.ok
