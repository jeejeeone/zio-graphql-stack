package com.zap.database

import anorm.*
import com.zap.database.model.util.TypeName

package object model:
  private def defaultDoesNotMatchLeft(value: Any, target: String, column: ColumnName): Left[TypeDoesNotMatch, Nothing] =
    Left(TypeDoesNotMatch(
      s"Cannot convert $value: ${value.asInstanceOf[AnyRef].getClass} to $target for column $column"
    ))

  inline def column[A]: Column[A] =
    Column.nonNull: (value, meta) =>
      value match
        case v: A => Right(v)
        case _ => defaultDoesNotMatchLeft(value, TypeName.of[A], meta.column)

  inline def columnAOrB[A, B]: Column[A | B] =
    Column.nonNull: (value, meta) =>
      value match
        case v: A => Right(v)
        case v: B => Right(v)
        case _ => defaultDoesNotMatchLeft(value, s"${TypeName.of[A]} or ${TypeName.of[B]}", meta.column)
