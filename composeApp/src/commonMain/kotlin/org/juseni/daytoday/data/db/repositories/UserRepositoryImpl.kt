package org.juseni.daytoday.data.db.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.juseni.daytoday.data.db.DayToDayDatabase
import org.juseni.daytoday.data.db.entities.toUser
import org.juseni.daytoday.domain.models.User
import org.juseni.daytoday.domain.models.toUserEntity
import org.juseni.daytoday.domain.repositories.UserRepository

class UserRepositoryImpl(
    private val database: DayToDayDatabase
): UserRepository {
    private val userDao by lazy { database.userDao() }

    override fun getUser(): Flow<User?> =
        userDao.getUser().map { it?.toUser() }

    override suspend fun insertUser(user: User) {
        userDao.insertUser(user.toUserEntity())
    }

    override suspend fun deleteUser() {
        userDao.deleteAll()
    }
}