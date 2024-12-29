package org.juseni.daytoday.domain.repositories

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    suspend fun setRememberMe(isRemembered: Boolean)
    fun getRememberMe(): Flow<Boolean>

    suspend fun setAutoLogging(isAutoLogged: Boolean)
    fun getAutoLogging(): Flow<Boolean>
}