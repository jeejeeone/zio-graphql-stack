package com.zap.graphql

import caliban.wrappers.Wrappers.printErrors
import caliban.{GraphQL, RootResolver, graphQL}
import com.zap.graphql.Operations.Queries
import com.zap.graphql.queries.PersonGraphQL
import com.zap.model.Token
import zio.{ZIO, ZLayer}

case class GraphQLApi(personGraphQL: PersonGraphQL):
  import SchemaDerivation.given

  val api: GraphQL[Token] = graphQL(RootResolver(Queries(personGraphQL.personsQuery()))) @@ printErrors

object GraphQLApi:
  val live = ZLayer.derive[GraphQLApi]
