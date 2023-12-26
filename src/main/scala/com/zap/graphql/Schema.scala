package com.zap.graphql

import com.zap.graphql.Schema.PersonResponse
import com.zap.model.{AddressId, CountryId, PersonId}
import zio.query.TaskQuery

object Schema:
  case class Country(id: CountryId, name: String)
  case class Address(id: AddressId, street: String, country: TaskQuery[Option[Country]])
  case class Person(name: String, id: PersonId, address: TaskQuery[Option[Address]])
  case class PersonResponse(persons: List[Person])

object Operations:
  case class Queries(persons: TaskQuery[PersonResponse])
