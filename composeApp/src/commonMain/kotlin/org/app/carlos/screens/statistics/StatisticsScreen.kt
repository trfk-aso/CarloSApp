package org.app.carlos.screens.statistics

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import carlosapp.composeapp.generated.resources.Res
import carlosapp.composeapp.generated.resources.bg_default
import carlosapp.composeapp.generated.resources.bg_marine
import carlosapp.composeapp.generated.resources.bg_midnight
import carlosapp.composeapp.generated.resources.bg_solaris
import carlosapp.composeapp.generated.resources.stat_default
import carlosapp.composeapp.generated.resources.stat_marine
import carlosapp.composeapp.generated.resources.stat_midnight
import carlosapp.composeapp.generated.resources.stat_solaris
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
import org.app.carlos.viewModel.SettingsViewModel
import org.app.carlos.viewModel.StatisticsViewModel
import org.app.carlos.viewModel.YearMonth
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    navController: NavHostController,
    viewModel: StatisticsViewModel = koinInject(),
    homeViewModel: HomeViewModel = koinInject(),
    searchViewModel: SearchViewModel = koinInject(),
    settingsViewModel: SettingsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    val settingsState by settingsViewModel.uiState.collectAsState()
    val selectedTheme = settingsState.themes.firstOrNull { it.isSelected }

    val backgroundRes = when (selectedTheme?.id) {
        "default" -> Res.drawable.bg_default
        "midnight" -> Res.drawable.bg_midnight
        "solaris" -> Res.drawable.bg_solaris
        "marine" -> Res.drawable.bg_marine
        else -> Res.drawable.bg_default
    }
    val overlayAlpha = if (selectedTheme?.id == "solaris") 0f else 0.4f

    val textColor = when (selectedTheme?.id) {
        "solaris" -> Color.Black
        else -> Color.White
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(backgroundRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = overlayAlpha))
        )

        Scaffold(
            bottomBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route
                BottomNavBar(
                    navController = navController,
                    currentRoute = currentRoute,
                    selectedThemeId = selectedTheme?.id
                )
            },
            containerColor = Color.Transparent
        ) { padding ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 8.dp),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Column {
                        Text(
                            "Statistics",
                            color = textColor,
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
                            onYearChange = { y -> viewModel.changeYear(y) },
                            selectedTheme = selectedTheme
                        )
                    }
                }

                if (!uiState.hasData) {
                    item {
                        val imageRes = when (selectedTheme?.id) {
                            "default" -> Res.drawable.stat_default
                            "midnight" -> Res.drawable.stat_midnight
                            "solaris" -> Res.drawable.stat_solaris
                            "marine" -> Res.drawable.stat_marine
                            else -> Res.drawable.stat_default
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 100.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Image(
                                painter = painterResource(imageRes),
                                contentDescription = null,
                                modifier = Modifier.size(350.dp)
                            )
                        }
                    }
                } else {
                    item {
                        val cardBackground = when (selectedTheme?.id) {
                            "default" -> Color(0xFF3E5CFF)
                            "midnight" -> Color(0xFF2C387B)
                            "solaris" -> Color(0xFFFFDCA5)
                            "marine" -> Color(0xFF22272E)
                            else -> Color(0xFF3E5CFF)
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = cardBackground)
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text("By Category", color = textColor)
                                Spacer(Modifier.height(8.dp))
                                PieChartComposable(data = uiState.byCategory, selectedTheme)
                            }
                        }
                    }

                    val cardBackground = when (selectedTheme?.id) {
                        "default" -> Color(0xFF3E5CFF)
                        "midnight" -> Color(0xFF2C387B)
                        "solaris" -> Color(0xFFFFDCA5)
                        "marine" -> Color(0xFF22272E)
                        else -> Color(0xFF3E5CFF)
                    }

                    if (uiState.period == PeriodType.YEAR) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = cardBackground)
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Text("Monthly Trend ${uiState.selectedYear}", color = textColor)
                                    Spacer(Modifier.height(8.dp))

                                    BarChartComposable(
                                        data = uiState.monthlyTrend,
                                        selectedTheme = selectedTheme,
                                        onBarClick = { month ->
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
                                    )
                                }
                            }
                        }
                    }

                    if (uiState.period == PeriodType.MONTH) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = cardBackground)
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    val monthLabel = monthName(uiState.selectedMonth)
                                    Text("Monthly Trend $monthLabel ${uiState.selectedYear}", color = textColor)
                                    Spacer(Modifier.height(8.dp))

                                    val monthData = mapOf(
                                        uiState.selectedMonth to (uiState.monthlyTrend[uiState.selectedMonth] ?: 0.0)
                                    )

                                    BarChartComposable(
                                        data = monthData,
                                        selectedTheme = selectedTheme,
                                        onBarClick = { month ->
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
                                    )
                                }
                            }
                        }
                    }

                    if (uiState.period == PeriodType.ALL) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = cardBackground)
                            ) {
                                Column(Modifier.padding(12.dp)) {
                                    Text("Monthly Trend (All time)", color = textColor)
                                    Spacer(Modifier.height(8.dp))

                                    BarChartComposable(
                                        data = uiState.monthlyTrend,
                                        selectedTheme = selectedTheme,
                                        onBarClick = { _ ->
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
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Averages",
                                color = when (selectedTheme?.id) {
                                    "default", "midnight", "marine" -> Color.White
                                    "solaris" -> Color.Black
                                    else -> Color.White
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            AveragesSection(
                                avgPerMonth = uiState.averagePerMonth,
                                averagesByCategory = uiState.averageByCategory,
                                selectedTheme = selectedTheme
                            )
                        }
                    }
                }
            }
        }
    }
}