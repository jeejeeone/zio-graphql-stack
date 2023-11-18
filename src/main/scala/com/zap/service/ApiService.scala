package com.zap.service

import com.zap.Schema.{Address, Person, PersonResponse}
import com.zap.model.{AddressId, PersonId}
import com.zap.repositories.AddressDataSource.GetAddress
import com.zap.repositories.PersonData.PersonRow
import com.zap.repositories.PersonDataSource.GetAllPersons
import com.zap.repositories.{AddressDataSource, PersonDataSource}
import zio.query.{TaskQuery, UQuery, ZQuery}
import zio.{URLayer, ZIO, ZLayer}

trait ApiService:
  def persons(): TaskQuery[PersonResponse]

object ApiService:
  val live: URLayer[AddressDataSource & PersonDataSource, ApiServiceLive] = ZLayer:
    for
      personDataSource  <- ZIO.service[PersonDataSource]
      addressDataSource <- ZIO.service[AddressDataSource]
    yield ApiServiceLive(personDataSource, addressDataSource)

case class ApiServiceLive(personDataSource: PersonDataSource, addressDataSource: AddressDataSource) extends ApiService:
  override def persons(): TaskQuery[PersonResponse] =
    val personsQuery: TaskQuery[List[PersonRow]] =
      ZQuery.fromRequest(GetAllPersons())(personDataSource.allPersonsDataSource)

    val getAddress = (id: AddressId) =>
      ZQuery.fromRequest(GetAddress(id))(addressDataSource.addressDataSource)
        .map(_.map(addressRow => Address(addressRow.id, addressRow.street)))

    personsQuery
      .map(_.map(row => Person(row.name, row.id, getAddress(row.addressId))))
      .map(PersonResponse(_))
