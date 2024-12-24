package org.juseni.daytoday.domain.models

data class User(
    val id: Int,
    val user: String,
    val password: String,
    val hasBills: Boolean,
    val hasIncomeExpenses: Boolean
)