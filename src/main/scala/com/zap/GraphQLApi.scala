package com.zap

import caliban.schema.Schema
import caliban.{RootResolver, graphQL}
import com.zap.Operations.Queries
import com.zap.Schema.{Address, Person, PersonResponse}
import com.zap.service.ApiService
import zio.{ZIO, ZLayer}

case class GraphQLApi(apiService: ApiService):
  import caliban.schema.Schema.auto.*

  val api = graphQL(RootResolver(Queries(apiService.persons())))

object GraphQLApi:
  val live = ZLayer:
    for
      apiService <- ZIO.service[ApiService]
    yield GraphQLApi(apiService)

