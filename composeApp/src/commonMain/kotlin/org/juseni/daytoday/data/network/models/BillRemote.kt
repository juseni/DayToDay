package org.juseni.daytoday.data.network.models

import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.juseni.daytoday.domain.models.Bill

@Serializable
data class BillRemote(
    @SerialName("id") val id: Int = 0,
    @SerialName("date") val date: String,
    @SerialName("tag") val tag: Int,
    @SerialName("amount") val amount: Double,
    @SerialName("description") val description: String?,
    @SerialName("user_app_id") val userId: Int = 0
)

fun BillRemote.toBill() = Bill(
    id = id,
    date = LocalDate.parse(date),
    tag = tag,
    amount = amount,
    description = description.orEmpty()
)