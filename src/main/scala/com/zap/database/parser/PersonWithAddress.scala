package com.zap.database.parser

import anorm.{Macro, RowParser, SqlParser, Success}
import com.zap.model.{AddressId, PersonId}

case class PersonWithAddress(personName: String, personId: PersonId, address: (AddressId, String))

object PersonWithAddress:
  val personWithAddressParser = Macro.namedParser[PersonWithAddress]
