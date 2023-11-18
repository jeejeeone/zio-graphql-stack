package com.zap.service

import com.zap.model.AddressId
import com.zap.repositories.PersonData
import com.zap.repositories.PersonData.AddressRow
import zio.{Task, ZIO, ZLayer}

trait AddressService:
  def getAddress(id: AddressId): Task[Option[AddressRow]]
  def getAddress(ids: List[AddressId]): Task[List[AddressRow]]

object AddressService:
  val live = ZLayer.succeed(AddressServiceLive())

case class AddressServiceLive() extends AddressService:
  override def getAddress(id: AddressId): Task[Option[AddressRow]] =
    ZIO.succeed(PersonData.addresses.find(a => a.id == id))
  override def getAddress(ids: List[AddressId]): Task[List[AddressRow]] =
    ZIO.succeed(
      PersonData.addresses.collect:
        case a if ids.contains(a.id) => a
    )
