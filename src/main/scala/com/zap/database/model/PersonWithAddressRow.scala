package com.zap.database.model

import anorm.*
import anorm.SqlParser.*
import com.clickhouse.data.value.UnsignedInteger
import com.zap.model.{AddressId, PersonId}

// Example for more complex type

case class PersonWithAddressRow(id: PersonId, name: String, address: (AddressId, String))

object PersonWithAddressRow:
  import ClickHouseTypes.*
  import Ids.*

  given Column[(AddressId, String)] =
    tuple[UnsignedInteger, String].map:
      case (a, b) => (AddressId(a.intValue()), b)

  val personWithAddressParser = Macro.indexedParser[PersonWithAddressRow]
