package com.example.moodatracker.ui.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarStyle
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import com.example.moodatracker.Data.MoodDistribution
import com.example.moodatracker.Model.MoodEntry
import com.example.moodatracker.ViewModel.MoodViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartScreen(navController: NavController, viewModel: MoodViewModel) {
    val moodDistribution by viewModel.moodDistribution.collectAsState()
    val moodHistory by viewModel.moodHistory.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mood Analytics", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFF3E5F5).copy(alpha = 0.5f)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFFF3E5F5), Color(0xFFE1BEE7))))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Mood Distribution",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp),
                color = Color(0xFF4A148C),
                fontWeight = FontWeight.Bold
            )
            if (moodDistribution.isNotEmpty()) {
                BarChartView(distribution = moodDistribution, viewModel = viewModel)
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No data available for charts")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Weekly Mood Trend",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp),
                color = Color(0xFF4A148C),
                fontWeight = FontWeight.Bold
            )
            if (moodHistory.isNotEmpty()) {
                LineChartView(history = moodHistory)
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No data available for the last 7 days")
                }
            }
        }
    }
}

@Composable
private fun BarChartView(distribution: List<MoodDistribution>, viewModel: MoodViewModel) {
    val barDataList = distribution.mapIndexed { index, item ->
        val moodOption = viewModel.availableMoods.find { it.displayName == item.mood }
        BarData(
            point = Point(index.toFloat(), item.count.toFloat()),
            color = Color(moodOption?.color ?: 0xFF666666)
        )
    }

    val xAxisData = AxisData.Builder()
        .axisStepSize(30.dp)
        .backgroundColor(Color.Transparent)
        .steps(distribution.size - 1)
        .labelData { index -> distribution.getOrNull(index)?.mood?.split(" ")?.firstOrNull() ?: "" }
        .axisLabelColor(Color(0xFF4A148C))
        .build()

    val yAxisData = AxisData.Builder()
        .steps(5)
        .backgroundColor(Color.Transparent)
        .axisLineColor(MaterialTheme.colorScheme.onSurface)
        .axisLabelColor(Color(0xFF4A148C))
        .labelData { index -> (index * 2).toString() }
        .build()

    val barChartData = BarChartData(
        chartData = barDataList,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        barStyle = BarStyle(barWidth = 20.dp),
        showYAxis = true,
        showXAxis = true
    )

    BarChart(
        modifier = Modifier.fillMaxWidth().height(300.dp),
        barChartData = barChartData
    )
}

@Composable
private fun LineChartView(history: List<MoodEntry>) {
    val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
    val fullDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val averageMoodsByDate = history
        .groupBy { fullDateFormat.format(it.timestamp) }
        .mapValues { (_, entries) ->
            if (entries.isEmpty()) 0.0 else entries.map { it.moodLevel }.average()
        }

    val calendar = Calendar.getInstance()
    val last7Days = (0..6).map {
        val date = calendar.time
        val dayLabel = dayFormat.format(date)
        val dateString = fullDateFormat.format(date)
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        dateString to dayLabel
    }.reversed()

    val points = last7Days.mapIndexed { index, (dateString, _) ->
        val moodLevel = averageMoodsByDate[dateString]?.toFloat() ?: 0f
        Point(index.toFloat(), moodLevel)
    }

    val xAxisLabels = last7Days.map { it.second }

    if (points.isNotEmpty()) {
        val xAxisData = AxisData.Builder()
            .axisStepSize(50.dp)
            .backgroundColor(Color.Transparent)
            .steps(xAxisLabels.size - 1)
            .axisLabelColor(Color(0xFF4A148C))
            .labelData { index -> xAxisLabels.getOrElse(index) { "" } }
            .build()

        val yAxisData = AxisData.Builder()
            .steps(4)
            .backgroundColor(Color.Transparent)
            .axisLabelColor(Color(0xFF4A148C))
            .labelData { index -> (index + 1).toString() }
            .build()

        val lineChartData = LineChartData(
            linePlotData = LinePlotData(
                lines = listOf(
                    Line(
                        dataPoints = points,
                        lineStyle = LineStyle(color = Color(0xFF7B1FA2)), // Primary purple
                        shadowUnderLine = ShadowUnderLine(alpha = 0.3f, brush = Brush.verticalGradient(colors = listOf(Color(0xFF7B1FA2), Color.Transparent))),
                        selectionHighlightPoint = SelectionHighlightPoint(color = Color(0xFF4A148C)),
                        selectionHighlightPopUp = SelectionHighlightPopUp()
                    )
                )
            ),
            xAxisData = xAxisData,
            yAxisData = yAxisData
        )

        LineChart(
            modifier = Modifier.fillMaxWidth().height(300.dp),
            lineChartData = lineChartData
        )
    }
}