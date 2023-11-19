package com.zap.graphql

import caliban.schema.Schema
import caliban.{RootResolver, graphQL}
import Operations.Queries
import com.zap.graphql.Schema.{Address, Person, PersonResponse}
import com.zap.graphql.queries.PersonGraphQL
import com.zap.model.{AddressId, PersonId}
import zio.{ZIO, ZLayer}

case class GraphQLApi(personGraphQL: PersonGraphQL):
  import caliban.schema.Schema.auto.*

  given Schema[Any, PersonId]  = Schema.intSchema.contramap(v => PersonId.unwrap(v))
  given Schema[Any, AddressId] = Schema.intSchema.contramap(v => AddressId.unwrap(v))

  val api = graphQL(RootResolver(Queries(personGraphQL.personsQuery())))

object GraphQLApi:
  val live = ZLayer:
    for
      personQuery <- ZIO.service[PersonGraphQL]
    yield GraphQLApi(personQuery)
