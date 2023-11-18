package com.zap

import caliban.schema.Schema
import caliban.{RootResolver, graphQL}
import com.zap.Operations.Queries
import com.zap.Schema.{Address, Person, PersonResponse}
import com.zap.model.{AddressId, PersonId}
import com.zap.service.ApiService
import zio.{ZIO, ZLayer}

case class GraphQLApi(apiService: ApiService):
  import caliban.schema.Schema.auto.*

  given Schema[Any, PersonId]  = Schema.intSchema.contramap(v => PersonId.unwrap(v))
  given Schema[Any, AddressId] = Schema.intSchema.contramap(v => AddressId.unwrap(v))

  val api = graphQL(RootResolver(Queries(apiService.persons())))

object GraphQLApi:
  val live = ZLayer:
    for
      apiService <- ZIO.service[ApiService]
    yield GraphQLApi(apiService)
