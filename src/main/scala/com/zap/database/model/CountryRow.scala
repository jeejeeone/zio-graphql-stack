package com.zap.database.model

import anorm.Macro
import com.zap.model.CountryId

case class CountryRow(id: CountryId, name: String)

object CountryRow:
  import Ids.given_Column_CountryId

  val countryParser = Macro.indexedParser[CountryRow]
