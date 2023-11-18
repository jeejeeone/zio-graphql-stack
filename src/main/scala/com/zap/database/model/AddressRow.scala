package com.zap.database.model

import anorm.Macro
import com.zap.model.AddressId

case class AddressRow(id: AddressId, street: String)

object AddressRow:
  import Ids.*
  val addressParser = Macro.indexedParser[AddressRow]
