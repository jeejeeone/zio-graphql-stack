package com.zap.database

import anorm.*
import com.zap.database.model.util.TypeName

package object model:
  def defaultDoesNotMatchLeft(value: Any, target: String, column: ColumnName): Left[TypeDoesNotMatch, Nothing] =
    Left(TypeDoesNotMatch(
      s"Cannot convert $value: ${value.asInstanceOf[AnyRef].getClass} to $target for column $column"
    ))

  inline def column[A] = columnTransform[A, A](v => v)

  inline def columnTransform[A, Result](f: A => Result) =
    Column.nonNull: (value, meta) =>
      value match
        case v: A => Right(f(v))
        case _ => defaultDoesNotMatchLeft(value, TypeName.of[A], meta.column)

  inline def columnTransform[A, B, Result](f: A | B => Result) =
    Column.nonNull: (value, meta) =>
      value match
        case v: A => Right(f(v))
        case v: B => Right(f(v))
        case _ => defaultDoesNotMatchLeft(value, s"${TypeName.of[A]} or ${TypeName.of[B]}", meta.column)
