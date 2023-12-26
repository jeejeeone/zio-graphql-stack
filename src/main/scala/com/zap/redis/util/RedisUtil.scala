package com.zap.redis.util

import zio.Chunk
import zio.schema.Schema
import zio.schema.codec.{DecodeError, ProtobufCodec}

import java.nio.charset.StandardCharsets

object RedisUtil:
  def encode[A](value: A)(using Schema[A]): ByteString =
    ByteString(new String(ProtobufCodec.protobufCodec.encode(value).toArray, StandardCharsets.UTF_8))

  def decode[A](value: ByteString)(using Schema[A]): Either[DecodeError, A] =
    ProtobufCodec.protobufCodec[A].decode(Chunk.fromArray(ByteString.unwrap(value).getBytes(StandardCharsets.UTF_8)))
