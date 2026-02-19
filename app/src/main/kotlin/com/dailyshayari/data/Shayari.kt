package com.dailyshayari.data

import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.serialization.Serializable

// Represents the document with the date ID (e.g., "2026-02-19")
@IgnoreExtraProperties
@Serializable
data class TodaysSpecialDocument(
    val shayari: List<Shayari> = emptyList()
)

// Represents a single shayari object within the 'shayari' array
@IgnoreExtraProperties
@Serializable
data class Shayari(
    val id: Long = 0,
    val category: String = "",
    val text: String = ""
)
