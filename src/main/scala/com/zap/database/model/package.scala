package com.zap.database
import anorm.*

package object model:
  def defaultDoesNotMatchLeft(value: Any, target: String, column: ColumnName): Left[TypeDoesNotMatch, Nothing] =
    Left(TypeDoesNotMatch(
      s"Cannot convert $value: ${value.asInstanceOf[AnyRef].getClass} to $target for column $column"
    ))
