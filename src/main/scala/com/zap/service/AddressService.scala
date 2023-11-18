package com.zap.service

import com.zap.database.ClickHouseQuery
import com.zap.database.model.Address
import com.zap.model.AddressId
import zio.{Task, URLayer, ZIO, ZLayer}

trait AddressService:
  def getAddress(id:  AddressId):       Task[Option[Address]]
  def getAddress(ids: List[AddressId]): Task[List[Address]]

object AddressService:
  val live: URLayer[ClickHouseQuery, AddressService] = ZLayer.derive[AddressServiceLive]

case class AddressServiceLive(clickHouseQuery: ClickHouseQuery) extends AddressService:
  override def getAddress(id: AddressId): Task[Option[Address]] =
    clickHouseQuery.addresses(id)

  override def getAddress(ids: List[AddressId]): Task[List[Address]] =
    clickHouseQuery.addresses(ids)
