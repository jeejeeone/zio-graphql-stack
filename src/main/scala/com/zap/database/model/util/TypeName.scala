package com.zap.database.model.util

object TypeName:
  import scala.quoted.*

  inline def of[A]: String = ${ impl[A] }

  def impl[A](
      using Type[A],
      Quotes,
    ): Expr[String] = Expr(Type.show[A])
