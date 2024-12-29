package org.juseni.daytoday.domain.models

import org.juseni.daytoday.data.db.entities.UserEntity

data class User(
    val id: Int = 0,
    val user: String,
    val password: String,
    val hasBills: Boolean,
    val hasIncomeExpenses: Boolean
)

fun User.toUserEntity() = UserEntity(
    id = id,
    user = user,
    password = password,
    hasBills = hasBills,
    hasIncomeExpenses = hasIncomeExpenses
)