package com.dailyshayari.data

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dailyshayari.R
import com.dailyshayari.db.FavoriteShayariDao
import com.dailyshayari.db.FavoriteShayariEntity
import com.dailyshayari.db.ShayariDao
import com.dailyshayari.db.ShayariEntity
import com.dailyshayari.di.FirebaseModule
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

class ShayariRepository(
    private val shayariDao: ShayariDao,
    private val favoriteShayariDao: FavoriteShayariDao,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val context: Context
) {

    private fun mapCategory(category: String?): String? {
        return when (category) {
            "Gita Lines" -> "gita"
            "Good Morning" -> "good_morning"
            "All" -> null
            null -> null
            else -> category.lowercase()
        }
    }

    fun getShayaris(selectedCategory: String?): Flow<PagingData<ShayariEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                val categoryKey = mapCategory(selectedCategory)
                if (categoryKey == null) {
                    shayariDao.getAllPaging()
                } else {
                    shayariDao.getByCategoryPaging(categoryKey)
                }
            }
        ).flow
    }

    suspend fun toggleFavorite(shayari: ShayariEntity, imageUrl: String) {
        val isFav = favoriteShayariDao.isFavorite(shayari.id).first()
        if (isFav) {
            favoriteShayariDao.delete(FavoriteShayariEntity(shayari.id, shayari.text, shayari.category, imageUrl))
        } else {
            favoriteShayariDao.insert(FavoriteShayariEntity(shayari.id, shayari.text, shayari.category, imageUrl))
        }
    }

    fun isFavorite(id: String): Flow<Boolean> = favoriteShayariDao.isFavorite(id)

    fun getAllFavorites(): Flow<List<FavoriteShayariEntity>> = favoriteShayariDao.getAllFavorites()

    fun getFavoriteCount(): Flow<Int> = favoriteShayariDao.getFavoriteCount()

    suspend fun searchShayaris(query: String, category: String?): List<ShayariEntity> {
        return withContext(Dispatchers.IO) {
            shayariDao.searchShayaris(query, mapCategory(category))
        }
    }

    suspend fun seedDatabaseIfNeeded() {
        withContext(Dispatchers.IO) {
            val count = shayariDao.getCount()
            if (count == 0) {
                val jsonString = context.resources.openRawResource(R.raw.shayari_seed).bufferedReader().use { it.readText() }
                val shayaris = Json.decodeFromString<List<ShayariEntity>>(jsonString)
                shayariDao.insertAll(shayaris)
            }
            fetchRemoteConfigIfNeeded()
        }
    }

    private suspend fun fetchRemoteConfigIfNeeded() {
        val lastFetch = userPreferencesRepository.lastFetchTime.first()
        val currentTime = System.currentTimeMillis()
        
        // Fetch once a day (24 hours = 86400000 ms)
        if (currentTime - lastFetch > TimeUnit.DAYS.toMillis(1)) {
            try {
                val remoteConfig = Firebase.remoteConfig
                val configSettings = remoteConfigSettings {
                    minimumFetchIntervalInSeconds = 3600 // Can fetch every hour if needed, but we check once a day
                }
                remoteConfig.setConfigSettingsAsync(configSettings)
                
                val fetched = remoteConfig.fetchAndActivate().await()
                if (fetched || lastFetch == 0L) {
                    val gitaMax = remoteConfig.getLong("gita_max").toInt().coerceAtLeast(1)
                    val randomMax = remoteConfig.getLong("random_max").toInt().coerceAtLeast(1)
                    val gmMax = remoteConfig.getLong("good_morning_max").toInt().coerceAtLeast(1)
                    val wdMax = remoteConfig.getLong("weekday_max").toInt().coerceAtLeast(1)
                    userPreferencesRepository.updateConfig(gitaMax, randomMax, gmMax, wdMax, currentTime)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getImageUrl(shayari: ShayariEntity): String {
        val category = mapCategory(shayari.category)
        
        val (folder, maxImages) = when (category) {
            "gita" -> "gita" to userPreferencesRepository.gitaMax.first()
            "good_morning" -> "good_morning" to userPreferencesRepository.goodMorningMax.first()
            else -> "random" to userPreferencesRepository.randomMax.first()
        }
        
        val imageIndex = (shayari.id.hashCode().absoluteValue % maxImages) + 1
        
        // Construct the Firebase Storage download URL
        return "${FirebaseModule.firebaseStorageBaseUrl}/${folder}%2F${imageIndex}.webp?alt=media"
    }
}
