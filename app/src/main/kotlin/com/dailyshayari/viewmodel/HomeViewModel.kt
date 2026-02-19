package com.dailyshayari.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dailyshayari.data.Shayari
import com.dailyshayari.datastore.ShayariDataStore
import com.dailyshayari.repository.ShayariRepository
import com.dailyshayari.repository.ShayariRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val shayariRepository: ShayariRepository) : ViewModel() {

    private val _todaysShayari = MutableStateFlow<List<Shayari>>(emptyList())
    val todaysShayari: StateFlow<List<Shayari>> = _todaysShayari.asStateFlow()

    init {
        fetchTodaysShayari()
    }

    private fun fetchTodaysShayari() {
        viewModelScope.launch {
            shayariRepository.getTodaysShayari()
                .catch { _todaysShayari.value = emptyList() } 
                .collect { shayaris ->
                    _todaysShayari.value = shayaris
                }
        }
    }
}

class HomeViewModelFactory(private val context: Context, private val firestore: FirebaseFirestore) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            val dataStore = ShayariDataStore(context)
            val repository = ShayariRepositoryImpl(firestore, dataStore)
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}