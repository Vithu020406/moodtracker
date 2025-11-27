package com.example.moodatracker.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "mood_entries")
data class MoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mood: String,
    val moodLevel: Int,
    val notes: String = "",
    val timestamp: Date = Date()
)
