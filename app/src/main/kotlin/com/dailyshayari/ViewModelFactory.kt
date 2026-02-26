package com.dailyshayari

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dailyshayari.data.ShayariRepository
import com.dailyshayari.data.UserPreferencesRepository
import com.dailyshayari.db.ShayariDatabase
import com.dailyshayari.ui.explore.ExploreViewModel

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExploreViewModel::class.java)) {
            val shayariDao = ShayariDatabase.getDatabase(context).shayariDao()
            val userPreferencesRepository = UserPreferencesRepository(context)
            val shayariRepository = ShayariRepository(shayariDao, userPreferencesRepository, context)
            return ExploreViewModel(shayariRepository) as T
        }
        if (modelClass.isAssignableFrom(CreateViewModel::class.java)) {
            return CreateViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
