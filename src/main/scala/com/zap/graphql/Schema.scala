package com.zap.graphql

import com.zap.graphql.Schema.PersonResponse
import com.zap.model.{AddressId, CountryId, PersonId, Token}
import zio.query.{RQuery, TaskQuery}

object Schema:
  import SchemaDerivation.given

  case class Country(id: CountryId, name: String) derives SchemaDerivation.SemiAuto
  case class Address(id: AddressId, street: String, country: TaskQuery[Option[Country]])
      derives SchemaDerivation.SemiAuto
  case class Person(name: String, id: PersonId, address: TaskQuery[Option[Address]]) derives SchemaDerivation.SemiAuto
  case class PersonResponse(persons: List[Person]) derives SchemaDerivation.SemiAuto

object Operations:
  import SchemaDerivation.given

  case class Queries(persons: RQuery[Token, PersonResponse]) derives SchemaDerivation.SemiAuto
