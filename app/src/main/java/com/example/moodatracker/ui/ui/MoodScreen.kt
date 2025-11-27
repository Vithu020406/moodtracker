package com.example.moodatracker.ui.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.moodatracker.Model.MoodEntry
import com.example.moodatracker.ViewModel.MoodViewModel
import com.example.moodatracker.navigation.Screen
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodScreen(navController: NavController, viewModel: MoodViewModel) {

    val moodHistory by viewModel.moodHistory.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mood Tracker", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.ChartScreen.route) }) {
                        Icon(Icons.Default.BarChart, contentDescription = "View Charts")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFF3E5F5).copy(alpha = 0.5f)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFF3E5F5), Color(0xFFE1BEE7))
                    )
                )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "How are you feeling today?",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color(0xFF4A148C),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        MoodSelector(viewModel)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Mood History",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF4A148C),
                     fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (moodHistory.isEmpty()) {
                    EmptyStateMessage()
                } else {
                    MoodHistoryList(moodHistory, navController, viewModel)
                }
            }
        }
    }
}

@Composable
private fun MoodSelector(viewModel: MoodViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        viewModel.availableMoods.forEach { moodOption ->
            val color by animateColorAsState(targetValue = Color(moodOption.color), label = "")
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { viewModel.addMoodEntry(moodOption.displayName, moodOption.level) }
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(moodOption.displayName.split(" ").last(), fontSize = 28.sp)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = moodOption.displayName.split(" ").first(),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EmptyStateMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No moods logged yet.\nTap a mood above to begin!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MoodHistoryList(history: List<MoodEntry>, navController: NavController, viewModel: MoodViewModel) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(history) { entry ->
            val moodOption = viewModel.availableMoods.find { it.displayName == entry.mood }
            val color = moodOption?.color?.let { Color(it) } ?: Color.Gray

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("${Screen.DetailScreen.route}/${entry.id}") },
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = entry.mood,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = formatDate(entry.timestamp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "View Details", tint = color)
                }
            }
        }
    }
}

private fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault())
    return formatter.format(date)
}
