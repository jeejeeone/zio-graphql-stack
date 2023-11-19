package com.zap.database.model

import anorm.Macro
import com.zap.model.{AddressId, PersonId}

case class PersonRow(id: PersonId, name: String, addressId: AddressId)

object PersonRow:
  import Ids.given_Column_PersonId
  import Ids.given_Column_AddressId
  val personParser = Macro.indexedParser[PersonRow]
