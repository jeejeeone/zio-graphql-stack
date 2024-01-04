package com.zap.redis.jedis

import com.zap.config.AppConfig
import redis.clients.jedis.{Jedis, JedisPool}
import zio.{Task, ZIO, ZLayer}

sealed trait JedisConnection:
  def connection: Task[Jedis]

case class JedisSingleConnection(jedis: () => Jedis) extends JedisConnection:
  override def connection: Task[Jedis] = ZIO.succeed(jedis())

case class JedisPooledConnection(pool: JedisPool) extends JedisConnection:
  override def connection: Task[Jedis] = ZIO.succeedBlocking(pool.getResource)

object JedisSingleConnection:
  val live =
    ZLayer:
      ZIO.config[AppConfig.RedisConfig](AppConfig.redisConfig).map: redisConfig =>
        JedisSingleConnection(() => new Jedis(redisConfig.redisUrl))
