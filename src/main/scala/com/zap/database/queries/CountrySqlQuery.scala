package com.zap.database.queries

import anorm.*
import com.zap.database.model.CountryRow
import com.zap.model.CountryId
import io.github.gaelrenoux.tranzactio.*
import io.github.gaelrenoux.tranzactio.anorm.*

object CountrySqlQuery:
  def countryQuery(ids: List[CountryId]): TranzactIO[List[CountryRow]] = tzio: c =>
    SQL(s"SELECT * FROM country WHERE id in [${ids.mkString(",")}]").as(CountryRow.countryParser.*)(c)

  def countryQuery(id: CountryId): TranzactIO[Option[CountryRow]] = tzio: c =>
    SQL(s"SELECT * FROM country WHERE id = $id").as(CountryRow.countryParser.singleOpt)(c)
