package com.dailyshayari.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dailyshayari.data.ShayariRepository
import com.dailyshayari.data.UserPreferencesRepository
import com.dailyshayari.db.ShayariEntity
import com.dailyshayari.di.FirebaseModule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlin.math.abs

class ExploreViewModel(
    private val shayariRepository: ShayariRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<String?>("All")
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    val shayaris: Flow<PagingData<ShayariEntity>> = _selectedCategory
        .flatMapLatest { category ->
            shayariRepository.getShayaris(category)
        }
        .cachedIn(viewModelScope)

    val gitaMax = userPreferencesRepository.gitaMax
    val randomMax = userPreferencesRepository.randomMax

    init {
        viewModelScope.launch {
            shayariRepository.seedDatabaseIfNeeded()
        }
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun toggleFavorite(shayari: ShayariEntity, imageUrl: String) {
        viewModelScope.launch {
            shayariRepository.toggleFavorite(shayari, imageUrl)
        }
    }

    fun isFavorite(id: String): Flow<Boolean> = shayariRepository.isFavorite(id)

    fun getImageUrl(shayari: ShayariEntity, gitaMax: Int, randomMax: Int): String {
        val category = shayari.category.lowercase()
        val isGita = category == "gita" || category == "gita lines"
        
        val maxImages = if (isGita) gitaMax else randomMax
        val folder = if (isGita) "gita" else "random"
        val imageIndex = (abs(shayari.id.hashCode()) % maxImages) + 1
        
        return "${FirebaseModule.firebaseStorageBaseUrl}/${folder}%2F${imageIndex}.webp?alt=media"
    }
}
