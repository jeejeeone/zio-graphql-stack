package com.zap.repositories

import com.zap.model.{AddressId, PersonId}

object PersonData:
  case class PersonRow(name: String, id: PersonId, addressId: AddressId)
  case class AddressRow(street: String, id: AddressId)

  val addresses =
    List(
      AddressRow(
        "ZAPstret",
        AddressId(1)
      ),
      AddressRow(
        "ZUPstret",
        AddressId(2)
      )
    )

  val persons =
    List(
      PersonRow(
        "zap",
        PersonId(1),
        AddressId(1)
      ),
      PersonRow(
        "zup",
        PersonId(2),
        AddressId(2)
      )
  )

