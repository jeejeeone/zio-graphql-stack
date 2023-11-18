package com.zap.repositories

import com.zap.model.AddressId
import com.zap.repositories.AddressDataSource.GetAddress
import com.zap.repositories.PersonData.AddressRow
import com.zap.service.AddressService
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
        override lazy val addressDataSource: UDataSource[GetAddress] = Batched.make[Any, GetAddress]("AddressDataSource"):
          requests =>
            val resultMap = CompletedRequestMap.empty
            requests.toList match
              case request :: Nil =>
                val result = addressService.getAddress(request.id)
                result.exit.map(e => resultMap.insert(request)(e))
              case batch =>
                val result = addressService.getAddress(batch.map(_.id))

                val res = result.fold(
                  err =>
                    requests.foldLeft(resultMap) { case (map, req) =>
                      map.insert(req)(Exit.fail(err))
                    },
                  success => success.foldLeft(resultMap) { case (map, address) =>
                    map.insert(GetAddress(address.id))(Exit.succeed(Some(address)))
                  }
                )

                res