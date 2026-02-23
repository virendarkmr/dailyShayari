package com.dailyshayari.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val IS_SEEDED = booleanPreferencesKey("is_seeded")
    }

    val isSeeded: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.IS_SEEDED] ?: false
        }

    suspend fun setIsSeeded(isSeeded: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_SEEDED] = isSeeded
        }
    }
}
