package com.zap.database.model

import anorm.Macro
import com.zap.model.{AddressId, PersonId}

case class Address(id: AddressId, street: String)

object Address:
  import Ids.*
  val addressParser = Macro.indexedParser[Address]