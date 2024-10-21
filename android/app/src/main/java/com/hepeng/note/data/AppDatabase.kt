package com.hepeng.note.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlin.concurrent.Volatile

@Database(entities = [Image::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): ImageDao

    companion object {
        private const val DB_NAME = "database-hepeng"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        DB_NAME
                    ).build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}