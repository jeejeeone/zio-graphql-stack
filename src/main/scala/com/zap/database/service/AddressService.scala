package com.zap.database.service

import com.zap.database.queries.AddressSqlQuery.addressQuery
import com.zap.database.model.AddressRow
import com.zap.model.AddressId
import io.github.gaelrenoux.tranzactio.anorm.Database
import zio.{Task, URLayer, ZIO, ZLayer}

trait AddressService:
  def getAddress(id:  AddressId):       Task[Option[AddressRow]]
  def getAddress(ids: List[AddressId]): Task[List[AddressRow]]

object AddressService:
  val live: URLayer[Database.Service, AddressService] = ZLayer.derive[AddressServiceLive]

case class AddressServiceLive(database: Database.Service) extends AddressService:
  override def getAddress(id: AddressId): Task[Option[AddressRow]] =
    database.autoCommitOrWiden(addressQuery(id))

  override def getAddress(ids: List[AddressId]): Task[List[AddressRow]] =
    database.autoCommitOrWiden(addressQuery(ids))
