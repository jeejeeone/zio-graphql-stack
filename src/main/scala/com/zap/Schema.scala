package com.zap

import com.zap.Schema.PersonResponse
import zio.query.{TaskQuery, UQuery}

object Schema:
  case class Address(id: Int, street: String)
  case class Person(
      name:    String,
      id:      Int,
      address: UQuery[Option[Address]],
    )
  case class PersonResponse(persons: List[Person])

object Operations:
  case class Queries(persons: TaskQuery[PersonResponse])
