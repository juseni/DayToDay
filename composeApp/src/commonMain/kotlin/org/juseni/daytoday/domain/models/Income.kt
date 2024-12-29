package org.juseni.daytoday.domain.models

import kotlinx.datetime.LocalDate
import org.juseni.daytoday.data.network.models.IncomeRemote

data class Income(
    val id: Int = 0,
    val date: LocalDate,
    val isRent: Boolean,
    val apartmentId: Int? = null,
    val rentApartmentTypeId: Int? = null,
    val amount: Double,
    val saleDescription: String? = null
)

fun Income.toIncomeRemote() = IncomeRemote(
    id = id,
    date = date.toString(),
    isRent = isRent,
    apartmentId = apartmentId,
    rentTypeId = rentApartmentTypeId,
    amount = amount,
    saleDescription = saleDescription
)
