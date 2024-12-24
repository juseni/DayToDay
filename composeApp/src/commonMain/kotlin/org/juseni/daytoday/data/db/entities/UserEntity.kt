package org.juseni.daytoday.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.juseni.daytoday.domain.models.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = false) val id: Int,
    @ColumnInfo(name = "user") val user: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "has_bills") val hasBills: Boolean,
    @ColumnInfo(name = "has_income_expenses") val hasIncomeExpenses: Boolean
)

fun UserEntity.toUser() = User(
    id = id,
    user = user,
    password = password,
    hasBills = hasBills,
    hasIncomeExpenses = hasIncomeExpenses
)