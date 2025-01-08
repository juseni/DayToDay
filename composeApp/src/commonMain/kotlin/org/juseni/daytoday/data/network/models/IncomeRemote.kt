package org.juseni.daytoday.data.network.models

import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.juseni.daytoday.domain.models.Income

@Serializable
data class IncomeRemote(
    @SerialName("id") val id: Int = 0,
    @SerialName("date") val date: String,
    @SerialName("rent_type_id") val rentType: Int,
    @SerialName("apartment_id") val apartmentId: Int?,
    @SerialName("rent_apartment_type_id") val rentTypeId: Int?,
    @SerialName("amount") val amount: Double,
    @SerialName("sale_description") val saleDescription: String?,
    @SerialName("user_app_id") val userId: Int = 0
)

fun IncomeRemote.toIncome() = Income(
    id = id,
    date = LocalDate.parse(date),
    rentType = rentType,
    apartmentId = apartmentId,
    rentApartmentTypeId = rentTypeId,
    amount = amount,
    saleDescription = saleDescription
)