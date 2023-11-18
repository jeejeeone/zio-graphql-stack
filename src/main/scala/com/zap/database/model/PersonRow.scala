package com.zap.database.model

import anorm.Macro
import com.zap.model.{AddressId, PersonId}

case class PersonRow(id: PersonId, name: String, addressId: AddressId)

object PersonRow:
  import Ids.*
  val personParser = Macro.indexedParser[PersonRow]
