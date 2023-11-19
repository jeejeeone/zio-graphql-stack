package com.zap.database.model

import anorm.*
import anorm.SqlParser.*
import com.clickhouse.data.value.UnsignedInteger
import com.zap.model.{AddressId, PersonId}

object Ids:
  given Column[PersonId] = columnTransform[UnsignedInteger, Int, PersonId]:
    case v: UnsignedInteger => PersonId(v.intValue())
    case v: Int             => PersonId(v)

  given Column[AddressId] = columnTransform[UnsignedInteger, Int, AddressId]:
    case v: UnsignedInteger => AddressId(v.intValue())
    case v: Int             => AddressId(v)
