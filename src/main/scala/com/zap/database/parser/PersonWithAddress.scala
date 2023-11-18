package com.zap.database.parser

import anorm.*
import anorm.SqlParser.*
import com.clickhouse.data.value.UnsignedInteger
import com.zap.model.{AddressId, PersonId}

case class PersonWithAddress(personId: PersonId, personName: String, address: (AddressId, String))

object PersonWithAddress:
  import ClickHouseTypes.*
  import Ids.*

  implicit val addressTuple: Column[(AddressId, String)] = tuple[UnsignedInteger, String].map:
    case (a, b) => (AddressId(a.intValue()), b)

  val personWithAddressParser = Macro.indexedParser[PersonWithAddress]
