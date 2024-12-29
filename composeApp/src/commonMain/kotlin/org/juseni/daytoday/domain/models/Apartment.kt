package org.juseni.daytoday.domain.models

import org.juseni.daytoday.data.network.models.ApartmentRemote

data class Apartment(
    val id: Int = 0,
    val name: String,
    val number: String,
    val percentage: Double,
    val isDirect: Boolean
)

fun Apartment.toApartmentRemote() = ApartmentRemote(
    id = id,
    name = name,
    number = number,
    percentage = percentage,
    isDirect = isDirect
)