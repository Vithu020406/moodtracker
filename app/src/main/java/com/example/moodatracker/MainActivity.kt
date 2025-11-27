package com.example.moodatracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moodatracker.Data.MoodDatabase
import com.example.moodatracker.ViewModel.MoodViewModel
import com.example.moodatracker.ViewModel.MoodViewModelFactory
import com.example.moodatracker.navigation.Screen
import com.example.moodatracker.ui.ui.ChartScreen
import com.example.moodatracker.ui.ui.MoodDetailScreen
import com.example.moodatracker.ui.ui.MoodScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val moodDao = MoodDatabase.getDatabase(applicationContext).moodDao()
        val viewModelFactory = MoodViewModelFactory(moodDao)
        val viewModel = ViewModelProvider(this, viewModelFactory)[MoodViewModel::class.java]

        setContent {
            MaterialTheme {
                Surface {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.MainScreen.route
                    ) {
                        composable(Screen.MainScreen.route) {
                            MoodScreen(navController = navController, viewModel = viewModel)
                        }
                        composable(Screen.ChartScreen.route) {
                            ChartScreen(navController = navController, viewModel = viewModel)
                        }
                        composable("${Screen.DetailScreen.route}/{entryId}") { backStackEntry ->
                            val entryId = backStackEntry.arguments?.getString("entryId")?.toLongOrNull()
                            if (entryId != null) {
                                MoodDetailScreen(
                                    navController = navController,
                                    entryId = entryId,
                                    viewModel = viewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
