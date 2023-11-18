package com.zap.database.model

import anorm.Macro
import com.zap.model.{AddressId, PersonId}

case class Person(id: PersonId, name: String, addressId: AddressId)

object Person:
  import Ids.*
  val personParser = Macro.indexedParser[Person]
