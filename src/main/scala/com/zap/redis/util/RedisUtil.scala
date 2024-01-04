package com.zap.redis.util

import zio.schema.Schema
import zio.schema.codec.{DecodeError, ProtobufCodec}

object RedisUtil:
  def encode[A: Schema](value: A): ByteString =
    ByteString.fromBytes(ProtobufCodec.protobufCodec.encode(value).toArray)

  def decode[A: Schema](value: ByteString): Either[DecodeError, A] =
    ProtobufCodec.protobufCodec[A].decode(ByteString.byteChunks(value))
