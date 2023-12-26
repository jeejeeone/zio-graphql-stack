package com.zap.redis

import com.zap.database.model.CountryRow
import com.zap.model.CountryId
import zio.schema.{DeriveSchema, Schema}

object Schemas:
  given Schema[CountryId]  = Schema[Int].transform(v => CountryId.apply(v), CountryId.unwrap)
  given Schema[CountryRow] = DeriveSchema.gen[CountryRow]
