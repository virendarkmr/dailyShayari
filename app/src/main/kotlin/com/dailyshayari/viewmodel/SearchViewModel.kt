package com.dailyshayari.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailyshayari.data.ShayariRepository
import com.dailyshayari.db.ShayariEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: ShayariRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>("All")
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    private val _searchResults = MutableStateFlow<List<ShayariEntity>>(emptyList())
    val searchResults: StateFlow<List<ShayariEntity>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelect(category: String?) {
        _selectedCategory.value = category
    }

    fun performSearch() {
        val query = _searchQuery.value.trim()
        val category = _selectedCategory.value

        viewModelScope.launch {
            _isSearching.value = true
            
            // Update recent searches
            if (query.isNotEmpty()) {
                val currentRecent = _recentSearches.value.toMutableList()
                currentRecent.remove(query)
                currentRecent.add(0, query)
                _recentSearches.value = currentRecent.take(5)
            }

            // Perform actual DB search using the repository we updated earlier
            val results = repository.searchShayaris(query, category)
            _searchResults.value = results
            _isSearching.value = false
        }
    }

    fun resetSearch() {
        _searchQuery.value = ""
        _selectedCategory.value = "All"
        _searchResults.value = emptyList()
    }

    fun onRecentSearchClick(query: String) {
        _searchQuery.value = query
        performSearch()
    }
}
