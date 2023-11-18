package com.zap.database.queries

import anorm.*
import com.zap.database.model.Address
import com.zap.model.AddressId
import io.github.gaelrenoux.tranzactio.*
import io.github.gaelrenoux.tranzactio.anorm.*

object AddressSqlQuery:
  def getAddresses = tzio: c =>
    SQL("SELECT * FROM address").as(Address.addressParser.*)(c)

  def getAddresses(ids: List[AddressId]) = tzio: c =>
    SQL(s"SELECT * FROM address WHERE id in [${ids.mkString(",")}]").as(Address.addressParser.*)(c)

  def getAddresses(id: AddressId) = tzio: c =>
    SQL(s"SELECT * FROM address WHERE id = $id").as(Address.addressParser.singleOpt)(c)
