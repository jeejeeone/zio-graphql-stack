package com.zap.redis.cache

import com.zap.redis.jedis.JedisClient
import com.zap.redis.util.{ByteString, RedisUtil}
import redis.clients.jedis.params.SetParams
import zio.schema.Schema
import zio.{Task, ZIO}

import scala.jdk.CollectionConverters.*
import scala.reflect.ClassTag

trait RedisCache[K, V]:
  // TODO: remove this formatting option
  def getOrUpdate(key:                K)(updateZIO:       K => Task[Option[V]]):     Task[Option[V]]
  def get(key:                        K): Task[Option[V]]
  def getMultiple(keys:               List[K]): Task[Map[K, V]]
  def getMultipleOrUpdate(keys:       List[K])(updateZIO: List[K] => Task[List[V]]): Task[Map[K, V]]
  def getMultipleOrUpdateAsList(keys: List[K])(updateZIO: List[K] => Task[List[V]]): Task[List[V]]

case class RedisCacheLive[K: ClassTag, V: Schema](
    jedisClient:   JedisClient,
    redisKey:      RedisKey[K, V],
    configuration: RedisCacheConfiguration = RedisCacheConfiguration.default,
  ) extends RedisCache[K, V]:
  private def decode(value: Option[ByteString]): Task[Option[V]] =
    value match
      case Some(value) => decode(value).map(v => Some(v))
      case None        => ZIO.succeed(None)

  private def decode(value: ByteString): Task[V] =
    ZIO.fromEither(RedisUtil.decode(value)).mapError(e =>
      new Throwable(s"Failed to decode: ${e.getMessage()}: $value", e)
    )

  def get(key: K): Task[Option[V]] =
    jedisClient.run(jedis => ByteString.fromNullable(jedis.get(redisKey.keyString(key))))
      .flatMap(decode)

  def getOrUpdate(key: K)(updateZIO: K => Task[Option[V]]): Task[Option[V]] =
    val setParams = configuration.expirationSeconds.fold(new SetParams())(expiration => new SetParams().ex(expiration))

    val updateRedisZIO = updateZIO(key).tap: item =>
      jedisClient.run(jedis => jedis.set(redisKey.keyString(key), ByteString.unwrap(RedisUtil.encode(item)), setParams))

    get(key).flatMap:
      case None => updateRedisZIO
      case v    => ZIO.succeed(v)

  def getMultiple(keys: List[K]): Task[Map[K, V]] =
    val redisKeys: Array[String] = keys.toArray.map(redisKey.keyString)

    jedisClient.run(jedis => jedis.mget(redisKeys*).asScala.toList.map(ByteString.fromNullable))
      .map(values => values.filter(_.nonEmpty).map(_.get))
      .flatMap(values => ZIO.foreach(values)(value => decode(value)))
      .map(values => values.map(v => (redisKey.keyFromValue(v), v)).toMap)

  def getMultipleOrUpdate(keys: List[K])(updateZIO: List[K] => Task[List[V]]): Task[Map[K, V]] =
    getMultiple(keys)
      .flatMap: values =>
        val missingKeys = keys.diff(values.keys.toList)

        val maybeUpdate =
          if missingKeys.nonEmpty then
            updateZIO(missingKeys)
          else
            ZIO.succeed(List.empty)

        val updateRedisZIO =
          maybeUpdate
            .tap:
              case Nil => ZIO.unit
              case fetchedValues =>
                jedisClient.run: jedis =>
                  val keysvalues =
                    fetchedValues.map(v => List(redisKey.keyStringFromValue(v), ByteString.unwrap(RedisUtil.encode(v))))
                      .flatten
                      .toArray

                  val multi = jedis.multi()

                  multi.mset(keysvalues*)

                  keysvalues.zipWithIndex.foreach:
                    case (v, i) if i % 2 == 0 && configuration.expirationSeconds.nonEmpty =>
                      multi.expire(v, configuration.expirationSeconds.get)
                    case _ => ()

                  multi.exec()
            .map: fetchedValues =>
              fetchedValues.map(v => (redisKey.keyFromValue(v), v)).toMap

        updateRedisZIO.map(_ ++ values)

  def getMultipleOrUpdateAsList(keys: List[K])(updateZIO: List[K] => Task[List[V]]): Task[List[V]] =
    getMultipleOrUpdate(keys)(updateZIO)
      .map(_.values.toList)

case class RedisCacheConfiguration(expirationSeconds: Option[Long])

object RedisCacheConfiguration:
  val default = RedisCacheConfiguration(None)
