package com.zap.redis.util

import zio.prelude.Newtype

object ByteString extends Newtype[String]:
  def fromNullable(value: String): Option[ByteString] =
    Option(value).map(v => ByteString(v))

type ByteString = ByteString.Type
