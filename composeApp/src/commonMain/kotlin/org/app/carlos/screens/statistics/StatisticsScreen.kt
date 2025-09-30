package org.app.carlos.screens.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.app.carlos.screens.Screen
import org.app.carlos.screens.bottom.BottomNavBar
import org.app.carlos.viewModel.HomeViewModel
import org.app.carlos.viewModel.PeriodType
import org.app.carlos.viewModel.SearchViewModel
import org.app.carlos.viewModel.StatisticsViewModel
import org.app.carlos.viewModel.YearMonth
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    navController: NavHostController,
    viewModel: StatisticsViewModel = koinInject(),
    homeViewModel: HomeViewModel = koinInject(),
    searchViewModel: SearchViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route
            BottomNavBar(navController, currentRoute)
        }
    ) { padding ->

        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF1E3A8A), Color(0xFF2563EB))
                    )
                )
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Column {
                        Text(
                            "Statistics",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        PeriodSelector(
                            selectedPeriod = uiState.period,
                            selectedYear = uiState.selectedYear,
                            selectedMonth = YearMonth(uiState.selectedYear, uiState.selectedMonth),
                            onPeriodChange = { viewModel.changePeriod(it) },
                            onMonthChange = { ym -> viewModel.changeMonth(ym.year, ym.month) },
                            onYearChange = { y -> viewModel.changeYear(y) }
                        )
                    }
                }

                if (!uiState.hasData) {
                    item {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(top = 64.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.BarChart,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("Not enough data for statistics.", color = Color.Gray)
                        }
                    }
                } else {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF2563EB))
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text("By Category", color = Color.White)
                                Spacer(Modifier.height(8.dp))
                                PieChartComposable(data = uiState.byCategory)
                            }
                        }
                    }

                    if (uiState.period == PeriodType.YEAR) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF2563EB))
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Text("Monthly Trend ${uiState.selectedYear}", color = Color.White)
                                    Spacer(Modifier.height(8.dp))

                                    BarChartComposable(data = uiState.monthlyTrend) { month ->
                                        scope.launch {
                                            val startOfMonth = LocalDate(uiState.selectedYear, month, 1)
                                            val endOfMonth = startOfMonth.plus(DatePeriod(months = 1))
                                                .minus(DatePeriod(days = 1))

                                            searchViewModel.search(
                                                query = null,
                                                categories = emptySet(),
                                                dateFrom = startOfMonth,
                                                dateTo = endOfMonth,
                                                amountMin = "",
                                                amountMax = ""
                                            )
                                        }
                                        navController.navigate(Screen.List.route)
                                    }
                                }
                            }
                        }
                    }

                    if (uiState.period == PeriodType.MONTH) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF2563EB))
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    val monthLabel = monthName(uiState.selectedMonth)
                                    Text("Monthly Trend $monthLabel ${uiState.selectedYear}", color = Color.White)
                                    Spacer(Modifier.height(8.dp))

                                    val monthData = mapOf(
                                        uiState.selectedMonth to (uiState.monthlyTrend[uiState.selectedMonth] ?: 0.0)
                                    )

                                    BarChartComposable(data = monthData) { month ->
                                        scope.launch {
                                            val startOfMonth = LocalDate(uiState.selectedYear, month, 1)
                                            val endOfMonth = startOfMonth.plus(DatePeriod(months = 1))
                                                .minus(DatePeriod(days = 1))

                                            searchViewModel.search(
                                                query = null,
                                                categories = emptySet(),
                                                dateFrom = startOfMonth,
                                                dateTo = endOfMonth,
                                                amountMin = "",
                                                amountMax = ""
                                            )
                                        }
                                        navController.navigate(Screen.List.route)
                                    }
                                }
                            }
                        }
                    }

                    if (uiState.period == PeriodType.ALL) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF2563EB))
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Text("Monthly Trend (All time)", color = Color.White)
                                    Spacer(Modifier.height(8.dp))

                                    BarChartComposable(data = uiState.monthlyTrend) { _ ->
                                        scope.launch {
                                            searchViewModel.search(
                                                query = null,
                                                categories = emptySet(),
                                                dateFrom = null,
                                                dateTo = null,
                                                amountMin = "",
                                                amountMax = ""
                                            )
                                        }
                                        navController.navigate(Screen.List.route)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        AveragesSection(
                            avgPerMonth = uiState.averagePerMonth,
                            averagesByCategory = uiState.averageByCategory
                        )
                    }
                }
            }
        }
    }
}