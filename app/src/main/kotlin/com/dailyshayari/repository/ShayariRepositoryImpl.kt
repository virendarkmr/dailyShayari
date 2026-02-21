package com.dailyshayari.repository

import com.dailyshayari.data.Shayari
import com.dailyshayari.data.TodaysSpecialDocument
import com.dailyshayari.datastore.ShayariDataStore
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class ShayariRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val dataStore: ShayariDataStore
) : ShayariRepository {

    override fun getTodaysShayari(): Flow<List<Shayari>> = flow {
        val today = getTodayDate()
        val lastFetchDate = dataStore.getLastFetchDate()

        if (lastFetchDate == today) {
            emit(dataStore.getCachedShayaris())
            return@flow
        }

        try {
            val document = firestore.collection("todays_special").document(today).get().await()
            val todaysSpecial = document.toObject(TodaysSpecialDocument::class.java)
            val shayaris = todaysSpecial?.shayari ?: emptyList()

            if (shayaris.isNotEmpty()) {
                dataStore.saveShayaris(shayaris, today)
                emit(shayaris)
            } else {
                // If today's document is empty, fetch the most recent one
                val recentDoc = firestore.collection("todays_special")
                    .orderBy("date", Query.Direction.DESCENDING)
                    .limit(1)
                    .get()
                    .await()

                if (!recentDoc.isEmpty) {
                    val recentShayaris = recentDoc.documents.first().toObject(TodaysSpecialDocument::class.java)?.shayari ?: emptyList()
                    if (recentShayaris.isNotEmpty()) {
                        dataStore.saveShayaris(recentShayaris, today) // Still save with today's date to prevent re-fetching
                        emit(recentShayaris)
                    } else {
                        emit(dataStore.getCachedShayaris()) // fallback to cache
                    }
                } else {
                    emit(dataStore.getCachedShayaris()) // fallback to cache
                }
            }
        } catch (e: Exception) {
            // If network fails, try to serve from cache anyway
            emit(dataStore.getCachedShayaris())
        }
    }.flowOn(Dispatchers.IO)

    private fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
