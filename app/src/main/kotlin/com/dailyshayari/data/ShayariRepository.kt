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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ShayariRepository(
    private val shayariDao: ShayariDao,
    private val favoriteShayariDao: FavoriteShayariDao,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val context: Context
) {

    private fun mapCategory(category: String?): String? {
        return when (category) {
            "Gita Lines" -> "gita"
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

    suspend fun toggleFavorite(shayari: ShayariEntity, imageName: String) {
        val isFav = favoriteShayariDao.isFavorite(shayari.id).first()
        if (isFav) {
            favoriteShayariDao.delete(FavoriteShayariEntity(shayari.id, shayari.text, shayari.category, imageName))
        } else {
            favoriteShayariDao.insert(FavoriteShayariEntity(shayari.id, shayari.text, shayari.category, imageName))
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
        }
    }
}
