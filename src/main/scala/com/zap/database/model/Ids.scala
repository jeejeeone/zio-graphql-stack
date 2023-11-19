package com.zap.database.model

import anorm.*
import anorm.SqlParser.*
import com.clickhouse.data.value.UnsignedInteger
import com.zap.model.{AddressId, PersonId}

object Ids:
  given Column[PersonId] =
    Column.nonNull { (value, meta) =>
      val MetaDataItem(qualified, nullable, clazz) = meta
      value match
        case v: UnsignedInteger => Right(PersonId(v.intValue()))
        case v: Int             => Right(PersonId(v))
        case _ => defaultDoesNotMatchLeft(value, "PersonId", qualified)
    }

  given Column[AddressId] =
    Column.nonNull { (value, meta) =>
      val MetaDataItem(qualified, nullable, clazz) = meta
      value match
        case v: UnsignedInteger => Right(AddressId(v.intValue()))
        case v: Int             => Right(AddressId(v))
        case _ => defaultDoesNotMatchLeft(value, "AddressId", qualified)
    }
