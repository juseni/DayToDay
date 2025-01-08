package org.juseni.daytoday.domain.models

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate
import org.juseni.daytoday.data.network.models.IncomeRemote

@Immutable
data class Income(
    val id: Int = 0,
    val date: LocalDate,
    val rentType: Int,
    val apartmentId: Int? = null,
    val rentApartmentTypeId: Int? = null,
    val amount: Double,
    val saleDescription: String? = null
)

fun Income.toIncomeRemote(userId: Int) = IncomeRemote(
    id = id,
    date = date.toString(),
    rentType = rentType,
    apartmentId = apartmentId,
    rentTypeId = rentApartmentTypeId,
    amount = amount,
    saleDescription = saleDescription,
    userId = userId
)
