package com.dailyshayari.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteShayariDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteShayari: FavoriteShayariEntity)

    @Delete
    suspend fun delete(favoriteShayari: FavoriteShayariEntity)

    @Query("SELECT * FROM favorite_shayaris ORDER BY favoritedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteShayariEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_shayaris WHERE id = :id)")
    fun isFavorite(id: String): Flow<Boolean>

    @Query("SELECT COUNT(*) FROM favorite_shayaris")
    fun getFavoriteCount(): Flow<Int>
}
