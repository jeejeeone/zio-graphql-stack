package com.zap.database.parser

import anorm.*
import anorm.SqlParser.*
import com.clickhouse.data.value.UnsignedByte

object ClickHouseTypes:
  implicit val columnToUnsignedByte: Column[UnsignedByte] =
    Column.nonNull { (value, meta) =>
      val MetaDataItem(qualified, nullable, clazz) = meta
      value match
        case v: UnsignedByte => Right(v)
        case _ => Left(TypeDoesNotMatch(
            s"Cannot convert $value: ${value.asInstanceOf[AnyRef].getClass} to Unsigned for column $qualified"
          ))
    }

  def unsignedByte(name: String): RowParser[UnsignedByte] = get[UnsignedByte](name)
