package com.dailyshayari.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dailyshayari.data.Shayari
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "shayari_cache")

class ShayariDataStore(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        private val LAST_FETCH_DATE_KEY = stringPreferencesKey("last_fetch_date")
        private val SHAYARI_LIST_KEY = stringPreferencesKey("shayari_list")
    }

    suspend fun saveShayaris(shayaris: List<Shayari>, date: String) {
        dataStore.edit {
            it[LAST_FETCH_DATE_KEY] = date
            it[SHAYARI_LIST_KEY] = Json.encodeToString(shayaris)
        }
    }

    suspend fun getCachedShayaris(): List<Shayari> {
        val json = dataStore.data.map { it[SHAYARI_LIST_KEY] }.first()
        return if (json != null) {
            Json.decodeFromString(json)
        } else {
            emptyList()
        }
    }

    suspend fun getLastFetchDate(): String? {
        return dataStore.data.map { it[LAST_FETCH_DATE_KEY] }.first()
    }
}