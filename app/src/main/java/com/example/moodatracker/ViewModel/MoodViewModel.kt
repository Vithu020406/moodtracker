package com.example.moodatracker.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodatracker.Data.MoodDao
import com.example.moodatracker.Data.MoodDistribution
import com.example.moodatracker.Model.MoodEntry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date

class MoodViewModel(private val moodDao: MoodDao) : ViewModel() {

    val moodHistory: StateFlow<List<MoodEntry>> = moodDao.getAllEntries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val moodDistribution: StateFlow<List<MoodDistribution>> = moodDao.getMoodDistribution()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val availableMoods = listOf(
        MoodOption("Happy 😊", 5, 0xFF4CAF50),
        MoodOption("Calm 🙂", 4, 0xFF42A5F5),
        MoodOption("Neutral 😐", 3, 0xFF9E9E9E),
        MoodOption("Sad 😟", 2, 0xFF5E35B1),
        MoodOption("Anxious 😬", 1, 0xFFFF9800)
    )

    fun addMoodEntry(mood: String, moodLevel: Int, notes: String = "") {
        viewModelScope.launch {
            val newEntry = MoodEntry(
                mood = mood,
                moodLevel = moodLevel,
                notes = notes,
                timestamp = Date()
            )
            moodDao.insertEntry(newEntry)
        }
    }

    fun updateMoodEntry(entry: MoodEntry) {
        viewModelScope.launch {
            moodDao.updateEntry(entry)
        }
    }

    fun deleteMoodEntry(entry: MoodEntry) {
        viewModelScope.launch {
            moodDao.deleteEntry(entry)
        }
    }

    suspend fun getEntryById(id: Long): MoodEntry? {
        return moodDao.getEntryById(id)
    }
}

data class MoodOption(
    val displayName: String,
    val level: Int,
    val color: Long
)
