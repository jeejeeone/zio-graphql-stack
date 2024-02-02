package com.zap.graphql

import caliban.schema.{GenericSchema, Schema}
import com.zap.model.{AddressId, CountryId, PersonId, Token}

object SchemaDerivation extends GenericSchema[Token]:
  given Schema[Token, PersonId]  = Schema.intSchema.contramap(PersonId.unwrap)
  given Schema[Token, AddressId] = Schema.intSchema.contramap(AddressId.unwrap)
  given Schema[Token, CountryId] = Schema.intSchema.contramap(CountryId.unwrap)
