package com.dailyshayari.repository

import com.dailyshayari.data.Shayari
import kotlinx.coroutines.flow.Flow

interface ShayariRepository {
    fun getTodaysShayari(): Flow<List<Shayari>>
}
