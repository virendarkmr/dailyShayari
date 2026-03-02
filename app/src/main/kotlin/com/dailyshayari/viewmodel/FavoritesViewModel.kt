package com.dailyshayari.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailyshayari.data.ShayariRepository
import com.dailyshayari.db.FavoriteShayariEntity
import com.dailyshayari.db.ShayariEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(private val shayariRepository: ShayariRepository) : ViewModel() {

    val favorites: StateFlow<List<FavoriteShayariEntity>> = shayariRepository.getAllFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleFavorite(shayari: ShayariEntity, imageName: String) {
        viewModelScope.launch {
            shayariRepository.toggleFavorite(shayari, imageName)
        }
    }
    
    fun toggleFavoriteFromFav(favorite: FavoriteShayariEntity) {
        viewModelScope.launch {
            val shayari = ShayariEntity(
                id = favorite.id,
                text = favorite.text,
                category = favorite.category,
                createdAt = 0, // Not needed for toggle
                updatedAt = 0
            )
            shayariRepository.toggleFavorite(shayari, favorite.imageName)
        }
    }

    fun isFavorite(id: String): Flow<Boolean> = shayariRepository.isFavorite(id)
}
