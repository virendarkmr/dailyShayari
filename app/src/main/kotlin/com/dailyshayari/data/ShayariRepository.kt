package com.dailyshayari.data

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dailyshayari.R
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
    private val userPreferencesRepository: UserPreferencesRepository,
    private val context: Context
) {

    fun getShayaris(selectedCategory: String?): Flow<PagingData<ShayariEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                if (selectedCategory == null || selectedCategory == "All") {
                    shayariDao.getAllPaging()
                } else {
                    shayariDao.getByCategoryPaging(selectedCategory.lowercase())
                }
            }
        ).flow
    }

    suspend fun seedDatabaseIfNeeded() {
        if (!userPreferencesRepository.isSeeded.first()) {
            withContext(Dispatchers.IO) {
                val jsonString = context.resources.openRawResource(R.raw.shayari_seed).bufferedReader().use { it.readText() }
                val shayaris = Json.decodeFromString<List<ShayariEntity>>(jsonString)
                shayariDao.insertAll(shayaris)
                userPreferencesRepository.setIsSeeded(true)
            }
        }
    }
}
