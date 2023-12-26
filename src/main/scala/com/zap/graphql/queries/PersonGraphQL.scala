package com.zap.graphql.queries

import com.zap.database.model.PersonRow
import com.zap.graphql.Schema.{Address, Country, Person, PersonResponse}
import com.zap.model.{AddressId, CountryId}
import com.zap.zquery.AddressDataSource.GetAddress
import com.zap.zquery.CountryDataSource.GetCountry
import com.zap.zquery.PersonDataSource.GetAllPersons
import com.zap.zquery.{AddressDataSource, CountryDataSource, PersonDataSource}
import zio.query.{TaskQuery, ZQuery}
import zio.{URLayer, ZIO, ZLayer}

trait PersonGraphQL:
  def personsQuery(): TaskQuery[PersonResponse]

object PersonGraphQL:
  val live: URLayer[AddressDataSource & PersonDataSource & CountryDataSource, PersonQueryLive] = ZLayer:
    for
      personDataSource  <- ZIO.service[PersonDataSource]
      addressDataSource <- ZIO.service[AddressDataSource]
      countryDataSource <- ZIO.service[CountryDataSource]
    yield PersonQueryLive(personDataSource, addressDataSource, countryDataSource)

case class PersonQueryLive(
    personDataSource:  PersonDataSource,
    addressDataSource: AddressDataSource,
    countryDataSource: CountryDataSource,
  ) extends PersonGraphQL:
  override def personsQuery(): TaskQuery[PersonResponse] =
    val personsQuery: TaskQuery[List[PersonRow]] =
      ZQuery.fromRequest(GetAllPersons())(personDataSource.allPersonsDataSource)

    def getCountry(id: CountryId): TaskQuery[Option[Country]] =
      ZQuery.fromRequest(GetCountry(id))(countryDataSource.countryDataSource)
        .map(_.map(countryRow => Country(countryRow.id, countryRow.name)))

    def getAddress(id: AddressId): TaskQuery[Option[Address]] =
      ZQuery.fromRequest(GetAddress(id))(addressDataSource.addressDataSource)
        .map(addressOption =>
          addressOption.map(addressRow =>
            Address(
              addressRow.id,
              addressRow.street,
              getCountry(addressRow.countryId),
            )
          )
        )

    personsQuery
      .map(rows => rows.map(row => Person(row.name, row.id, getAddress(row.addressId))))
      .map(PersonResponse.apply)
