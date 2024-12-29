package org.juseni.daytoday.data.sharedpreferences.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.juseni.daytoday.domain.repositories.DataStoreRepository

/*
  Represents Shared Preferences DataStore
 */
class DataStoreRepositoryImpl(
    private val dataStore: DataStore<Preferences>
): DataStoreRepository {

    private val rememberMe = booleanPreferencesKey("remember_me")
    private val autoLogging = booleanPreferencesKey("auto_logging")

    override suspend fun setRememberMe(isRemembered: Boolean) {
        dataStore.edit { settings ->
            settings[rememberMe] = isRemembered
        }
    }

    override fun getRememberMe(): Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[rememberMe] ?: false
        }

    override suspend fun setAutoLogging(isAutoLogged: Boolean) {
        dataStore.edit { settings ->
            settings[autoLogging] = isAutoLogged
        }
    }

    override fun getAutoLogging(): Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[autoLogging] ?: false
        }
}