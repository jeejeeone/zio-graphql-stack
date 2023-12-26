package com.zap.database.model

import anorm.*
import anorm.SqlParser.*
import com.zap.model.{AddressId, CountryId, PersonId}

object Ids:
  given Column[PersonId]  = ClickHouseTypes.given_Column_UnsignedInteger.map(v => PersonId(v.intValue()))
  given Column[AddressId] = ClickHouseTypes.given_Column_UnsignedInteger.map(v => AddressId(v.intValue()))
  given Column[CountryId] = ClickHouseTypes.given_Column_UnsignedInteger.map(v => CountryId(v.intValue()))
