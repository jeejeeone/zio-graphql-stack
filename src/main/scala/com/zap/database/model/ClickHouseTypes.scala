package com.zap.database.model

import anorm.*
import anorm.SqlParser.*
import com.clickhouse.data.value.{UnsignedByte, UnsignedInteger}

object ClickHouseTypes:
  given Column[UnsignedByte] =
    Column.nonNull { (value, meta) =>
      val MetaDataItem(qualified, nullable, clazz) = meta
      value match
        case v: UnsignedByte => Right(v)
        case _ => defaultDoesNotMatchLeft(value, "UnsignedByte", qualified)
    }

  given Column[UnsignedInteger] =
    Column.nonNull { (value, meta) =>
      val MetaDataItem(qualified, nullable, clazz) = meta
      value match
        case v: UnsignedInteger => Right(v)
        case _ => defaultDoesNotMatchLeft(value, "UnsignedInteger", qualified)
    }

  def tuple[A, B] =
    Column.nonNull { (value, meta) =>
      val MetaDataItem(qualified, nullable, clazz) = meta
      value match
        case array: java.util.List[?] =>
          val value = (array.get(0).asInstanceOf[A], array.get(1).asInstanceOf[B])
          Right(value)
        // TODO: Fixme
        case _ => defaultDoesNotMatchLeft(value, "TODO fixme", qualified)
    }
