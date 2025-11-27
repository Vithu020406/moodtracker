package com.example.moodatracker.Data

import androidx.room.*
import com.example.moodatracker.Model.MoodEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDao {
    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<MoodEntry>>

    @Query("SELECT * FROM mood_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): MoodEntry?

    @Query("SELECT mood, COUNT(*) as count FROM mood_entries GROUP BY mood")
    fun getMoodDistribution(): Flow<List<MoodDistribution>>

    @Insert
    suspend fun insertEntry(entry: MoodEntry)

    @Update
    suspend fun updateEntry(entry: MoodEntry)

    @Delete
    suspend fun deleteEntry(entry: MoodEntry)
}
