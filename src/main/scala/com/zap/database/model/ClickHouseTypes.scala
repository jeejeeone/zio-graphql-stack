package com.zap.database.model

import anorm.*
import anorm.SqlParser.*
import com.clickhouse.data.value.{UnsignedByte, UnsignedInteger}

object ClickHouseTypes:
  given Column[UnsignedByte]    = column[UnsignedByte]
  given Column[UnsignedInteger] = column[UnsignedInteger]

  def tuple[A, B]: Column[(A, B)] =
    column[java.util.List[?]].map: list =>
      (list.get(0).asInstanceOf[A], list.get(1).asInstanceOf[B])
