package org.juseni.daytoday.data.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.juseni.daytoday.data.db.entities.UserEntity
import org.juseni.daytoday.domain.models.User

@Serializable
data class UserRemote(
    @SerialName("id") val id: Int = 0,
    @SerialName("user_name") val user: String,
    @SerialName("password") val password: String,
    @SerialName("has_bills") val hasBills: Boolean,
    @SerialName("has_income_expenses") val hasIncomeExpenses: Boolean
)

fun UserRemote.toUser() = User(
    user = user,
    password = password,
    hasBills = hasBills,
    hasIncomeExpenses = hasIncomeExpenses
)

fun UserRemote.toUserEntity() = UserEntity(
    id = id,
    user = user,
    password = password,
    hasBills = hasBills,
    hasIncomeExpenses = hasIncomeExpenses
)