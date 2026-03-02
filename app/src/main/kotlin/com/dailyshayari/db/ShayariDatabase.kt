package com.dailyshayari.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ShayariEntity::class, FavoriteShayariEntity::class], version = 2, exportSchema = false)
abstract class ShayariDatabase : RoomDatabase() {

    abstract fun shayariDao(): ShayariDao
    abstract fun favoriteShayariDao(): FavoriteShayariDao

    companion object {
        @Volatile
        private var INSTANCE: ShayariDatabase? = null

        fun getDatabase(context: Context): ShayariDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ShayariDatabase::class.java,
                    "shayari_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
