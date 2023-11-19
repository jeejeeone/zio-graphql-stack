package com.zap

import com.zap.database.ClickHouseDatabase
import com.zap.database.service.{AddressService, PersonService}
import com.zap.graphql.GraphQLApi
import com.zap.graphql.queries.PersonGraphQL
import com.zap.http.HttpApp
import com.zap.observability.metrics.MetricsLayers
import com.zap.zquery.{AddressDataSource, PersonDataSource}
import zio.*

object App extends ZIOAppDefault:
  import zio.http.*

  override def run =
    (for
      _       <- ZIO.logInfo("Start")
      httpApp <- HttpApp.httpAppZIO
      _       <- Server.serve(httpApp).provide(Server.default)
    yield ())
      .provide(
        PersonGraphQL.live,
        PersonService.live,
        AddressService.live,
        GraphQLApi.live,
        AddressDataSource.live,
        PersonDataSource.live,
        ClickHouseDatabase.clickHouseDatabase,
        MetricsLayers.metricsLayers,
      )
