package com.zap.redis.jedis

import redis.clients.jedis.Jedis
import zio.{RIO, Task, URLayer, ZIO, ZLayer}

trait JedisClient:
  def runZIO[R, E, A](jedisZIO: Jedis => RIO[R, A]): RIO[R, A]
  def run[A](jedisFn:           Jedis => A):         Task[A]

case class JedisClientLive(jedisConnection: JedisConnection) extends JedisClient:
  def runZIO[R, E, A](jedisZIO: Jedis => RIO[R, A]): RIO[R, A] =
    jedisConnection.connection.acquireReleaseWithAuto(jedis => jedisZIO(jedis))

  def run[A](jedisFn: Jedis => A): Task[A] =
    runZIO(jedis => ZIO.attemptBlocking(jedisFn(jedis)))

object JedisClient:
  val live: URLayer[JedisConnection, JedisClientLive] = ZLayer.derive[JedisClientLive]
