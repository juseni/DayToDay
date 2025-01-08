package org.juseni.daytoday.data.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.juseni.daytoday.domain.models.Apartment

@Serializable
data class ApartmentRemote(
    @SerialName("id") val id: Int = 0,
    @SerialName("name") val name: String,
    @SerialName("number") val number: String,
    @SerialName("percentage") val percentage: Double,
    @SerialName("is_direct") val isDirect: Boolean
)

fun ApartmentRemote.toApartment() = Apartment(
    id = id,
    name = name,
    number = number,
    percentage = percentage,
    isDirect = isDirect
)