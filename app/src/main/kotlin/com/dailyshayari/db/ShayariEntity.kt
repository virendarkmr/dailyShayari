package com.dailyshayari.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "shayaris")
data class ShayariEntity(
    @PrimaryKey
    val id: String,
    val text: String,
    val category: String,
    val createdAt: Long,
    val updatedAt: Long
)
