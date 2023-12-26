package com.zap.zquery

import com.zap.database.model.CountryRow
import com.zap.database.queries.CountrySqlQuery.countryQuery
import com.zap.model.CountryId
import com.zap.redis.cache.{RedisCacheLive, RedisKey}
import com.zap.redis.jedis.JedisClient
import com.zap.zquery.CountryDataSource.GetCountry
import io.github.gaelrenoux.tranzactio.anorm.Database
import zio.query.DataSource.Batched
import zio.query.{CompletedRequestMap, DataSource, Request}
import zio.{Exit, URLayer, ZIO, ZLayer}
import com.zap.redis.Schemas.given_Schema_CountryRow

trait CountryDataSource:
  def countryDataSource: UDataSource[GetCountry]

object CountryDataSource:
  case class GetCountry(id: CountryId) extends Request[Nothing, Option[CountryRow]]

  val live: URLayer[Database.Service & JedisClient, CountryDataSource] =
    ZLayer:
      for
        jedisClient <- ZIO.service[JedisClient]
        database    <- ZIO.service[Database.Service]
        redisCache = RedisCacheLive[CountryId, CountryRow](jedisClient, RedisKey.countryKey)
      yield new CountryDataSource:
        override lazy val countryDataSource: UDataSource[GetCountry] =
          Batched.make[Any, GetCountry]("CountryDataSource"): requests =>
            val resultMap = CompletedRequestMap.empty
            requests.toList match
              case request :: Nil =>
                redisCache.getOrUpdate(request.id)(key => database.autoCommitOrWiden(countryQuery(key))).exit.map(e =>
                  resultMap.insert(request)(e)
                )
              case batch =>
                redisCache.getMultipleOrUpdateAsList(batch.map(_.id))(keys =>
                  database.autoCommitOrWiden(countryQuery(keys))
                ).fold(
                  err =>
                    requests.foldLeft(resultMap) { case (map, req) =>
                      map.insert(req)(Exit.fail(err))
                    },
                  success =>
                    success.foldLeft(resultMap) { case (map, country) =>
                      map.insert(GetCountry(country.id))(Exit.succeed(Some(country)))
                    },
                )
