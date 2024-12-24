package org.juseni.daytoday.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.juseni.daytoday.domain.models.User

interface UserRepository {
    fun getUserById(userId: Int): Flow<User?>
    suspend fun insertUser(user: User)
}