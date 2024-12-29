package org.juseni.daytoday.data.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.juseni.daytoday.domain.models.RentApartmentType

@Serializable
data class RentApartmentTypeRemote(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String
)

fun RentApartmentTypeRemote.toRentApartmentType() = RentApartmentType(
    id = id,
    name = name
)