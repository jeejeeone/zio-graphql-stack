package com.zap.database.model

import anorm.*
import anorm.SqlParser.*
import com.clickhouse.data.value.{UnsignedByte, UnsignedInteger}
import com.zap.model.{AddressId, PersonId}

object Ids:
  implicit val columnPersonId: Column[PersonId] =
    Column.nonNull { (value, meta) =>
      val MetaDataItem(qualified, nullable, clazz) = meta
      value match
        case v: UnsignedByte    => Right(PersonId(v.intValue()))
        case v: UnsignedInteger => Right(PersonId(v.intValue()))
        case v: Int             => Right(PersonId(v))
        case _ => Left(TypeDoesNotMatch(
            s"Cannot convert $value: ${value.asInstanceOf[AnyRef].getClass} to Unsigned for column $qualified"
          ))
    }

  implicit val columnAddressId: Column[AddressId] =
    Column.nonNull { (value, meta) =>
      val MetaDataItem(qualified, nullable, clazz) = meta
      value match
        case v: UnsignedByte    => Right(AddressId(v.intValue()))
        case v: UnsignedInteger => Right(AddressId(v.intValue()))
        case v: Int             => Right(AddressId(v))
        case _ => Left(TypeDoesNotMatch(
            s"Cannot convert $value: ${value.asInstanceOf[AnyRef].getClass} to Unsigned for column $qualified"
          ))
    }
