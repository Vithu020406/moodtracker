package com.example.moodatracker.ui.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.moodatracker.Model.MoodEntry
import com.example.moodatracker.ViewModel.MoodViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodDetailScreen(
    navController: NavController,
    entryId: Long,
    viewModel: MoodViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    var moodEntry by remember { mutableStateOf<MoodEntry?>(null) }
    var notes by remember { mutableStateOf("") }
    val showDeleteDialog = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = entryId) {
        coroutineScope.launch {
            val entry = viewModel.getEntryById(entryId)
            moodEntry = entry
            notes = entry?.notes ?: ""
        }
    }

    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text("Delete Entry") },
            text = { Text("Are you sure you want to delete this mood entry?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        moodEntry?.let { viewModel.deleteMoodEntry(it) }
                        showDeleteDialog.value = false
                        navController.popBackStack()
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Mood Entry") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog.value = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Entry")
                    }
                }
            )
        }
    ) { paddingValues ->
        moodEntry?.let { entry ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Mood: ${entry.mood}", style = MaterialTheme.typography.headlineSmall)

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth().height(150.dp)
                )

                Button(
                    onClick = {
                        val updatedEntry = entry.copy(notes = notes)
                        viewModel.updateMoodEntry(updatedEntry)
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Changes")
                }
            }
        }
    }
}