package com.zap.database.queries

import anorm.*
import com.zap.database.model.AddressRow
import com.zap.model.AddressId
import io.github.gaelrenoux.tranzactio.*
import io.github.gaelrenoux.tranzactio.anorm.*

object AddressSqlQuery:
  def getAddresses = tzio: c =>
    SQL("SELECT * FROM address").as(AddressRow.addressParser.*)(c)

  def getAddresses(ids: List[AddressId]) = tzio: c =>
    SQL(s"SELECT * FROM address WHERE id in [${ids.mkString(",")}]").as(AddressRow.addressParser.*)(c)

  def getAddresses(id: AddressId) = tzio: c =>
    SQL(s"SELECT * FROM address WHERE id = $id").as(AddressRow.addressParser.singleOpt)(c)
