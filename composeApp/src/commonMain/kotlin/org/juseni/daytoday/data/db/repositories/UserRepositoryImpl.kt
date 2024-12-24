package org.juseni.daytoday.data.db.repositories

import kotlinx.coroutines.flow.Flow
import org.juseni.daytoday.data.db.DayToDayDatabase
import org.juseni.daytoday.domain.models.User
import org.juseni.daytoday.domain.repositories.UserRepository

class UserRepositoryImpl(
    private val database: DayToDayDatabase
): UserRepository {
    private val userDao by lazy { database.userDao() }

    override fun getUserById(userId: Int): Flow<User?> {
        TODO("Not yet implemented")
    }

    override suspend fun insertUser(user: User) {
        TODO("Not yet implemented")
    }
}