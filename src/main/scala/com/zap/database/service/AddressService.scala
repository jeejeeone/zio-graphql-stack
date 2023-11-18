package com.zap.database.service

import com.zap.database.queries.AddressSqlQuery.getAddresses
import com.zap.database.model.Address
import com.zap.model.AddressId
import io.github.gaelrenoux.tranzactio.anorm.Database
import zio.{Task, URLayer, ZIO, ZLayer}

trait AddressService:
  def getAddress(id:  AddressId):       Task[Option[Address]]
  def getAddress(ids: List[AddressId]): Task[List[Address]]

object AddressService:
  val live: URLayer[Database.Service, AddressService] = ZLayer.derive[AddressServiceLive]

case class AddressServiceLive(chDatabase: Database.Service) extends AddressService:
  override def getAddress(id: AddressId): Task[Option[Address]] =
    chDatabase.autoCommitOrWiden(getAddresses(id))

  override def getAddress(ids: List[AddressId]): Task[List[Address]] =
    chDatabase.autoCommitOrWiden(getAddresses(ids))
