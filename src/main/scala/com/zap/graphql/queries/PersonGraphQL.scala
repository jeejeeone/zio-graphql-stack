package com.zap.graphql.queries

import com.zap.database.model.PersonRow
import com.zap.graphql.Schema.{Address, Country, Person, PersonResponse}
import com.zap.model.{AddressId, CountryId, Token}
import com.zap.zquery.AddressDataSource.GetAddress
import com.zap.zquery.CountryDataSource.GetCountry
import com.zap.zquery.PersonDataSource.GetAllPersons
import com.zap.zquery.{AddressDataSource, CountryDataSource, PersonDataSource}
import zio.{ZIO, ZLayer}
import zio.query.{RQuery, TaskQuery, ZQuery}

trait PersonGraphQL:
  def personsQuery(): RQuery[Token, PersonResponse]

object PersonGraphQL:
  val live = ZLayer.derive[PersonQueryLive]

case class PersonQueryLive(
    personDataSource:  PersonDataSource,
    addressDataSource: AddressDataSource,
    countryDataSource: CountryDataSource,
  ) extends PersonGraphQL:
  override def personsQuery(): RQuery[Token, PersonResponse] =
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

    ZQuery.serviceWithZIO[Token](t => ZIO.logInfo(s"HELLO: $t")) *>
      personsQuery
        .map(rows => rows.map(row => Person(row.name, row.id, getAddress(row.addressId))))
        .map(PersonResponse.apply)
