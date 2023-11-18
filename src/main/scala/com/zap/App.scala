package com.zap

import caliban.quick.*
import com.zap.App.Environment
import com.zap.database.ClickHouseDatabase
import com.zap.database.service.{AddressService, PersonService}
import com.zap.graphql.GraphQLApi
import com.zap.graphql.queries.PersonGraphQL
import com.zap.model.AddressId
import com.zap.zquery.{AddressDataSource, PersonDataSource}
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
      PersonGraphQL.live,
      PersonService.live,
      AddressService.live,
      GraphQLApi.live,
      AddressDataSource.live,
      PersonDataSource.live,
      ClickHouseDatabase.clickHouseDatabase,
    )
