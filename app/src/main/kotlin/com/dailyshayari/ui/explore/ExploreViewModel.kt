package com.dailyshayari.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dailyshayari.data.ShayariRepository
import com.dailyshayari.db.ShayariEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class ExploreViewModel(private val shayariRepository: ShayariRepository) : ViewModel() {

    private val _selectedCategory = MutableStateFlow<String?>("All")
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    val shayaris: Flow<PagingData<ShayariEntity>> = _selectedCategory
        .flatMapLatest { category ->
            shayariRepository.getShayaris(category)
        }
        .cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            shayariRepository.seedDatabaseIfNeeded()
        }
    }

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }
}
