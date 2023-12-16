package com.zap.graphql.queries

import com.zap.database.model.PersonRow
import com.zap.graphql.Schema.{Address, Person, PersonResponse}
import com.zap.model.AddressId
import com.zap.zquery.AddressDataSource.GetAddress
import com.zap.zquery.PersonDataSource.GetAllPersons
import com.zap.zquery.{AddressDataSource, PersonDataSource}
import zio.query.{TaskQuery, ZQuery}
import zio.{URLayer, ZIO, ZLayer}

trait PersonGraphQL:
  def personsQuery(): TaskQuery[PersonResponse]

object PersonGraphQL:
  val live: URLayer[AddressDataSource & PersonDataSource, PersonQueryLive] = ZLayer:
    for
      personDataSource  <- ZIO.service[PersonDataSource]
      addressDataSource <- ZIO.service[AddressDataSource]
    yield PersonQueryLive(personDataSource, addressDataSource)

case class PersonQueryLive(personDataSource: PersonDataSource, addressDataSource: AddressDataSource)
    extends PersonGraphQL:
  override def personsQuery(): TaskQuery[PersonResponse] =
    val personsQuery: TaskQuery[List[PersonRow]] =
      ZQuery.fromRequest(GetAllPersons())(personDataSource.allPersonsDataSource)

    def getAddress(id: AddressId): TaskQuery[Option[Address]] =
      ZQuery.fromRequest(GetAddress(id))(addressDataSource.addressDataSource)
        .map(addressOption => addressOption.map(addressRow => Address(addressRow.id, addressRow.street)))

    personsQuery
      .map(rows => rows.map(row => Person(row.name, row.id, getAddress(row.addressId))))
      .map(PersonResponse.apply)
