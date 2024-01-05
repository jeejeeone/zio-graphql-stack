package com.zap.graphql

import caliban.schema.Schema
import caliban.{RootResolver, graphQL}
import Operations.Queries
import caliban.tracing.TracingWrapper
import caliban.wrappers.Wrappers.printErrors
import com.zap.graphql.Schema.{Person, PersonResponse}
import com.zap.graphql.queries.PersonGraphQL
import com.zap.model.{AddressId, CountryId, PersonId}
import zio.{ZIO, ZLayer}

case class GraphQLApi(personGraphQL: PersonGraphQL):
  import caliban.schema.Schema.auto.*

  given Schema[Any, PersonId]  = Schema.intSchema.contramap(PersonId.unwrap)
  given Schema[Any, AddressId] = Schema.intSchema.contramap(AddressId.unwrap)
  given Schema[Any, CountryId] = Schema.intSchema.contramap(CountryId.unwrap)

  val api = graphQL(RootResolver(Queries(personGraphQL.personsQuery()))) @@ printErrors @@ TracingWrapper.traced

object GraphQLApi:
  val live = ZLayer.derive[GraphQLApi]
