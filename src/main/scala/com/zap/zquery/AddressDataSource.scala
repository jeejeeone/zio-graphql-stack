package com.zap.zquery

import com.zap.database.model.AddressRow
import com.zap.database.service.AddressService
import com.zap.model.AddressId
import com.zap.zquery.AddressDataSource.GetAddress
import zio.query.DataSource.Batched
import zio.query.{CompletedRequestMap, DataSource, Request}
import zio.{Exit, URLayer, ZIO, ZLayer}

trait AddressDataSource:
  def addressDataSource: UDataSource[GetAddress]

object AddressDataSource:
  case class GetAddress(id: AddressId) extends Request[Nothing, Option[AddressRow]]

  val live: URLayer[AddressService, AddressDataSource] =
    ZLayer:
      for
        addressService <- ZIO.service[AddressService]
      yield new AddressDataSource:
        override lazy val addressDataSource: UDataSource[GetAddress] =
          Batched.make[Any, GetAddress]("AddressDataSource"): requests =>
            val resultMap = CompletedRequestMap.empty
            requests.toList match
              case request :: Nil =>
                addressService.getAddress(request.id).exit.map(e => resultMap.insert(request)(e))
              case batch =>
                addressService.getAddress(batch.map(_.id)).fold(
                  err =>
                    requests.foldLeft(resultMap) { case (map, req) =>
                      map.insert(req)(Exit.fail(err))
                    },
                  success =>
                    success.foldLeft(resultMap) { case (map, address) =>
                      map.insert(GetAddress(address.id))(Exit.succeed(Some(address)))
                    },
                )
