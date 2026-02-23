package com.dailyshayari.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ShayariDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(shayaris: List<ShayariEntity>)

    @Query("SELECT * FROM shayaris ORDER BY createdAt DESC")
    fun getAllPaging(): PagingSource<Int, ShayariEntity>

    @Query("SELECT * FROM shayaris WHERE category = :category ORDER BY createdAt DESC")
    fun getByCategoryPaging(category: String): PagingSource<Int, ShayariEntity>
}
