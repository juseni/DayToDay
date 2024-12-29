package org.juseni.daytoday.domain.models

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDate
import org.juseni.daytoday.data.network.models.BillRemote

@Immutable
data class Bill(
    val id: Int,
    val date: LocalDate,
    val tag: Int,
    val amount: Double,
    val description: String
)

fun Bill.toBillRemote(userId: Int) = BillRemote(
    date = date.toString(),
    tag = tag,
    amount = amount,
    description = description,
    userId = userId
)