package com.dailyshayari.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dailyshayari.data.Shayari
import com.dailyshayari.data.UserPreferencesRepository
import com.dailyshayari.datastore.ShayariDataStore
import com.dailyshayari.db.ShayariDatabase
import com.dailyshayari.db.ShayariEntity
import com.dailyshayari.di.FirebaseModule
import com.dailyshayari.repository.ShayariRepository
import com.dailyshayari.repository.ShayariRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.abs

class HomeViewModel(
    private val remoteRepository: ShayariRepository,
    private val localRepository: com.dailyshayari.data.ShayariRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _todaysShayari = MutableStateFlow<List<Shayari>>(emptyList())
    val todaysShayari: StateFlow<List<Shayari>> = _todaysShayari.asStateFlow()

    val randomMax = userPreferencesRepository.randomMax

    init {
        fetchTodaysShayari()
    }

    private fun fetchTodaysShayari() {
        viewModelScope.launch {
            remoteRepository.getTodaysShayari()
                .catch { _todaysShayari.value = emptyList() } 
                .collect { shayaris ->
                    _todaysShayari.value = shayaris
                }
        }
    }

    fun toggleFavorite(shayari: Shayari, imageUrl: String) {
        viewModelScope.launch {
            val entity = ShayariEntity(
                id = shayari.id.toString(),
                text = shayari.text,
                category = shayari.category,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            localRepository.toggleFavorite(entity, imageUrl)
        }
    }

    fun isFavorite(id: Long): Flow<Boolean> = localRepository.isFavorite(id.toString())

    fun getImageUrl(shayari: Shayari, randomMax: Int): String {
        val imageIndex = (abs(shayari.id.toString().hashCode()) % randomMax) + 1
        return "${FirebaseModule.firebaseStorageBaseUrl}/random%2F${imageIndex}.webp?alt=media"
    }
}

class HomeViewModelFactory(private val context: Context, private val firestore: FirebaseFirestore) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            val dataStore = ShayariDataStore(context)
            val remoteRepository = ShayariRepositoryImpl(firestore, dataStore)
            
            val database = ShayariDatabase.getDatabase(context)
            val userPreferencesRepository = UserPreferencesRepository(context)
            val localRepository = com.dailyshayari.data.ShayariRepository(
                database.shayariDao(),
                database.favoriteShayariDao(),
                userPreferencesRepository,
                context
            )
            
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(remoteRepository, localRepository, userPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
