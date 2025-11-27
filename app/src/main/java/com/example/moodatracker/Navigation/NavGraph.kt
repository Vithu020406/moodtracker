package com.example.moodatracker.Navigation

sealed class Screen(val route: String) {
    object MainScreen : Screen("main_screen")
    object DetailScreen : Screen("detail_screen")
    object ChartScreen : Screen("chart_screen")
}