package com.dailyshayari.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val IS_SEEDED = booleanPreferencesKey("is_seeded")
        val GITA_MAX = intPreferencesKey("gita_max")
        val RANDOM_MAX = intPreferencesKey("random_max")
        val GOOD_MORNING_MAX = intPreferencesKey("good_morning_max")
        val LAST_FETCH_TIME = longPreferencesKey("last_fetch_time")
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

    val gitaMax: Flow<Int> = context.dataStore.data.map { it[PreferencesKeys.GITA_MAX] ?: 5 }
    val randomMax: Flow<Int> = context.dataStore.data.map { it[PreferencesKeys.RANDOM_MAX] ?: 10 }
    val goodMorningMax: Flow<Int> = context.dataStore.data.map { it[PreferencesKeys.GOOD_MORNING_MAX] ?: 5 }
    val lastFetchTime: Flow<Long> = context.dataStore.data.map { it[PreferencesKeys.LAST_FETCH_TIME] ?: 0L }

    suspend fun updateConfig(gitaMax: Int, randomMax: Int, goodMorningMax: Int, fetchTime: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.GITA_MAX] = gitaMax
            preferences[PreferencesKeys.RANDOM_MAX] = randomMax
            preferences[PreferencesKeys.GOOD_MORNING_MAX] = goodMorningMax
            preferences[PreferencesKeys.LAST_FETCH_TIME] = fetchTime
        }
    }
}
