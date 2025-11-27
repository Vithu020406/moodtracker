package com.example.moodatracker.Data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.moodatracker.Model.MoodEntry

@Database(
    entities = [MoodEntry::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MoodDatabase : RoomDatabase() {
    abstract fun moodDao(): MoodDao

    companion object {
        @Volatile
        private var INSTANCE: MoodDatabase? = null

        fun getDatabase(context: Context): MoodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MoodDatabase::class.java,
                    "mood_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}