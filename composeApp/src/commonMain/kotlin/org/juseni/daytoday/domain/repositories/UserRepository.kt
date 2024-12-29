package org.juseni.daytoday.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.juseni.daytoday.domain.models.User

interface UserRepository {
    fun getUser(): Flow<User?>
    suspend fun insertUser(user: User)
    suspend fun deleteUser()

}