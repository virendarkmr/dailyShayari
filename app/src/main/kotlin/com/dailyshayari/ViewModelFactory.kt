package com.dailyshayari

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dailyshayari.data.ShayariRepository
import com.dailyshayari.data.UserPreferencesRepository
import com.dailyshayari.db.ShayariDatabase
import com.dailyshayari.ui.explore.ExploreViewModel
import com.dailyshayari.viewmodel.FavoritesViewModel
import com.dailyshayari.viewmodel.SearchViewModel
import com.dailyshayari.viewmodel.SettingsViewModel

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val database = ShayariDatabase.getDatabase(context)
        val userPreferencesRepository = UserPreferencesRepository(context)
        val shayariRepository = ShayariRepository(
            database.shayariDao(),
            database.favoriteShayariDao(),
            userPreferencesRepository,
            context
        )

        return when {
            modelClass.isAssignableFrom(ExploreViewModel::class.java) -> {
                ExploreViewModel(shayariRepository) as T
            }
            modelClass.isAssignableFrom(CreateViewModel::class.java) -> {
                CreateViewModel() as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(shayariRepository) as T
            }
            modelClass.isAssignableFrom(FavoritesViewModel::class.java) -> {
                FavoritesViewModel(shayariRepository) as T
            }
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> {
                SearchViewModel(shayariRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
