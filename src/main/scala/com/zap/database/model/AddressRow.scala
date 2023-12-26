package com.zap.database.model

import anorm.Macro
import com.zap.model.{AddressId, CountryId}

case class AddressRow(id: AddressId, street: String, countryId: CountryId)

object AddressRow:
  import Ids.given_Column_AddressId
  import Ids.given_Column_CountryId

  val addressParser = Macro.indexedParser[AddressRow]
