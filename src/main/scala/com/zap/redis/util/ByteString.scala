package com.zap.redis.util

import zio.Chunk
import zio.prelude.Newtype

import java.nio.charset.StandardCharsets

object ByteString extends Newtype[String]:
  def fromNullableString(value: String): Option[ByteString] =
    Option(value).map(ByteString.wrap)

  def fromBytes(bytes: Array[Byte]): ByteString =
    ByteString(new String(bytes, StandardCharsets.UTF_8))

  def byteChunks(value: ByteString): Chunk[Byte] =
    Chunk.fromArray(ByteString.unwrap(value).getBytes(StandardCharsets.UTF_8))

type ByteString = ByteString.Type
