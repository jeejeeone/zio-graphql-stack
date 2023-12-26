package com.zap.model

import zio.prelude.Newtype

object PersonId extends Newtype[Int]
type PersonId = PersonId.Type

object AddressId extends Newtype[Int]
type AddressId = AddressId.Type

object CountryId extends Newtype[Int]
type CountryId = CountryId.Type
