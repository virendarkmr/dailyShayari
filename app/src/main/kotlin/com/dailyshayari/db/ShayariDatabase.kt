package com.dailyshayari.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dailyshayari.data.Notification

@Database(entities = [ShayariEntity::class, FavoriteShayariEntity::class, Notification::class], version = 3, exportSchema = false)
abstract class ShayariDatabase : RoomDatabase() {

    abstract fun shayariDao(): ShayariDao
    abstract fun favoriteShayariDao(): FavoriteShayariDao
    abstract fun notificationDao(): NotificationDao

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
