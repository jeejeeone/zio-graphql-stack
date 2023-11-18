package com.zap

import caliban.quick.*
import com.zap.Main.Environment
import com.zap.repositories.{AddressDataSource, PersonDataSource}
import com.zap.service.{AddressService, ApiService, PersonService}
import zio.*

object Main extends ZIOAppDefault:
  override def run: ZIO[Environment & ZIOAppArgs & Scope, Any, Any] =
    (ZIO.serviceWithZIO[GraphQLApi]:
      graphQlApi =>
        graphQlApi.api.runServer(
          port = 8080,
          apiPath = "/api/graphql",
          graphiqlPath = Some("/graphiql")
        ))
      .provide(
        ApiService.live,
        PersonService.live,
        AddressService.live,
        GraphQLApi.live,
        AddressDataSource.live,
        PersonDataSource.live
      )
