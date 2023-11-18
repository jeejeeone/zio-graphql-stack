package com.zap.graphql.queries

import com.zap.graphql.Schema.{Address, Person, PersonResponse}
import com.zap.model.AddressId
import com.zap.repositories.AddressDataSource.GetAddress
import com.zap.repositories.PersonData.PersonRow
import com.zap.repositories.PersonDataSource.GetAllPersons
import com.zap.repositories.{AddressDataSource, PersonDataSource}
import zio.query.{TaskQuery, ZQuery}
import zio.{URLayer, ZIO, ZLayer}

trait PersonQuery:
  def persons(): TaskQuery[PersonResponse]

object PersonQuery:
  val live: URLayer[AddressDataSource & PersonDataSource, PersonQueryLive] = ZLayer:
    for
      personDataSource  <- ZIO.service[PersonDataSource]
      addressDataSource <- ZIO.service[AddressDataSource]
    yield PersonQueryLive(personDataSource, addressDataSource)

case class PersonQueryLive(personDataSource: PersonDataSource, addressDataSource: AddressDataSource)
    extends PersonQuery:
  override def persons(): TaskQuery[PersonResponse] =
    val personsQuery: TaskQuery[List[PersonRow]] =
      ZQuery.fromRequest(GetAllPersons())(personDataSource.allPersonsDataSource)

    val getAddress = (id: AddressId) =>
      ZQuery.fromRequest(GetAddress(id))(addressDataSource.addressDataSource)
        .map(_.map(addressRow => Address(addressRow.id, addressRow.street)))

    personsQuery
      .map(_.map(row => Person(row.name, row.id, getAddress(row.addressId))))
      .map(PersonResponse(_))
