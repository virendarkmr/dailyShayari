package com.dailyshayari.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_shayaris")
data class FavoriteShayariEntity(
    @PrimaryKey
    val id: String,
    val text: String,
    val category: String,
    val imageUrl: String, // Store the URL of the background image from Firebase Storage
    val favoritedAt: Long = System.currentTimeMillis()
)
