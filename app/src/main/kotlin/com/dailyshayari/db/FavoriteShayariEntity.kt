package com.dailyshayari.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_shayaris")
data class FavoriteShayariEntity(
    @PrimaryKey
    val id: String,
    val text: String,
    val category: String,
    val imageName: String, // Store the name of the background image (e.g., "bg_1")
    val favoritedAt: Long = System.currentTimeMillis()
)
