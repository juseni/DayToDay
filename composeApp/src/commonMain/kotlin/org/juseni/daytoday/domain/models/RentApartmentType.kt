package org.juseni.daytoday.domain.models

import org.juseni.daytoday.data.network.models.RentApartmentTypeRemote

data class RentApartmentType(
    val id: Int,
    val name: String
)

fun RentApartmentType.toRentApartmentTypeRemote() = RentApartmentTypeRemote(
    id = id,
    name = name
)
