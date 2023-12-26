package com.zap.redis.cache

import com.zap.database.model.CountryRow
import com.zap.model.CountryId

trait RedisKey[K, V]:
  def keyString(key:            K): String
  def keyFromValue(value:       V): K
  def keyStringFromValue(value: V): String = keyString(keyFromValue(value))

object RedisKey:
  val countryKey = new RedisKey[CountryId, CountryRow]:
    def keyString(key:      CountryId):  String    = s"country-$key"
    def keyFromValue(value: CountryRow): CountryId = value.id
