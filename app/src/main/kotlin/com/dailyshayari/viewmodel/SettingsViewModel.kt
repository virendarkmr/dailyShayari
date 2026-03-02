package com.dailyshayari.viewmodel

import androidx.lifecycle.ViewModel
import com.dailyshayari.data.ShayariRepository
import kotlinx.coroutines.flow.Flow

class SettingsViewModel(private val shayariRepository: ShayariRepository) : ViewModel() {
    val favoriteCount: Flow<Int> = shayariRepository.getFavoriteCount()
}
