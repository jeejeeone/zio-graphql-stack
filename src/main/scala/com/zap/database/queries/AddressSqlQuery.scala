package com.zap.database.queries

import anorm.*
import com.zap.database.model.AddressRow
import com.zap.model.AddressId
import io.github.gaelrenoux.tranzactio.*
import io.github.gaelrenoux.tranzactio.anorm.*

object AddressSqlQuery:
  def addressQuery: TranzactIO[List[AddressRow]] = tzio: c =>
    SQL("SELECT * FROM address").as(AddressRow.addressParser.*)(c)

  def addressQuery(ids: List[AddressId]): TranzactIO[List[AddressRow]] = tzio: c =>
    SQL(s"SELECT * FROM address WHERE id in [${ids.mkString(",")}]").as(AddressRow.addressParser.*)(c)

  def addressQuery(id: AddressId): TranzactIO[Option[AddressRow]] = tzio: c =>
    SQL(s"SELECT * FROM address WHERE id = $id").as(AddressRow.addressParser.singleOpt)(c)
