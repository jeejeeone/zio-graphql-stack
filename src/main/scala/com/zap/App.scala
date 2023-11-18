package com.zap

import caliban.quick.*
import com.zap.App.Environment
import com.zap.database.{ClickHouseConnection, ClickHouseQuery}
import com.zap.graphql.GraphQLApi
import com.zap.graphql.queries.PersonQuery
import com.zap.model.AddressId
import com.zap.repositories.{AddressDataSource, PersonDataSource}
import com.zap.service.{AddressService, PersonService}
import zio.*

object App extends ZIOAppDefault:
  override def run: ZIO[Environment & ZIOAppArgs & Scope, Any, Any] =
    ZIO.serviceWithZIO[GraphQLApi]: graphQlApi =>
      graphQlApi.api.runServer(
        port = 8080,
        apiPath = "/api/graphql",
        graphiqlPath = Some("/graphiql"),
      )
    .provide(
      PersonQuery.live,
      PersonService.live,
      AddressService.live,
      GraphQLApi.live,
      AddressDataSource.live,
      PersonDataSource.live,
      ClickHouseConnection.clickHouseConnection,
      ClickHouseQuery.live,
    )
