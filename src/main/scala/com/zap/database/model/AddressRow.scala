package com.zap.database.model

import anorm.Macro
import com.zap.model.AddressId

case class AddressRow(id: AddressId, street: String)

object AddressRow:
  import Ids.given_Column_AddressId
  val addressParser = Macro.indexedParser[AddressRow]
