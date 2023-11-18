package com.zap

import caliban.quick.*
import com.zap.config.AppConfig.{HttpServerConfig, httpServerConfig}
import com.zap.database.ClickHouseDatabase
import com.zap.database.service.{AddressService, PersonService}
import com.zap.graphql.GraphQLApi
import com.zap.graphql.queries.PersonGraphQL
import com.zap.zquery.{AddressDataSource, PersonDataSource}
import zio.*

object App extends ZIOAppDefault:
  override def run =
    (for
      _                <- ZIO.logInfo("Start")
      httpServerConfig <- ZIO.config[HttpServerConfig](httpServerConfig)
      graphQlApi       <- ZIO.service[GraphQLApi]
      _ <- graphQlApi.api.runServer(
        port = httpServerConfig.httpPort,
        apiPath = "/api/graphql",
        graphiqlPath = Some("/graphiql"),
      )
    yield ())
      .provide(
        PersonGraphQL.live,
        PersonService.live,
        AddressService.live,
        GraphQLApi.live,
        AddressDataSource.live,
        PersonDataSource.live,
        ClickHouseDatabase.clickHouseDatabase,
      )
