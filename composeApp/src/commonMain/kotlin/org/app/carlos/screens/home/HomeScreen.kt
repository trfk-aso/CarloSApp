package org.app.carlos.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.app.carlos.screens.Screen
import org.app.carlos.screens.bottom.BottomNavBar
import org.app.carlos.screens.search.SearchScreen
import org.app.carlos.viewModel.HomeViewModel
import org.app.carlos.viewModel.PeriodType
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: open Settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("${Screen.AddEditExpense.route}?expenseId=-1") }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Expense")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavBar(navController, currentRoute)
        }
    ) { paddingValues ->
        if (uiState.isEmpty) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No expenses yet", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { navController.navigate("${Screen.AddEditExpense.route}?expenseId=-1") }) {
                        Text("Add your first expense")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Column {
                        Text("This Month", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "$${uiState.totalThisMonth}",
                            style = MaterialTheme.typography.headlineLarge
                        )
                        val diff = uiState.totalThisMonth - uiState.totalLastMonth
                        val trendSymbol = when {
                            diff > 0 -> "↑"
                            diff < 0 -> "↓"
                            else -> "→"
                        }

                        Text(
                            buildAnnotatedString {
                                append("Last month: $${uiState.totalLastMonth} ")
                                withStyle(SpanStyle(color = Color.Red)) {
                                    append(trendSymbol)
                                }
                            },
                            style = MaterialTheme.typography.bodySmall
                        )


                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(onClick = { viewModel.changePeriod(PeriodType.MONTH) }) { Text("Month") }
                            Button(onClick = { viewModel.changePeriod(PeriodType.YEAR) }) { Text("Year") }
                            Button(onClick = { viewModel.changePeriod(PeriodType.ALL) }) { Text("All") }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = { navController.navigate("${Screen.AddEditExpense.route}?expenseId=-1") }) { Text("+ Expense") }
                        Button(onClick = { /* Statistics */ }) { Text("Statistics") }
                        Button(onClick = { /* History */ }) { Text("History") }
                    }
                }

                item {
                    Column {
                        Text("By Category", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        val categories = listOf("Fuel", "Insurance", "Repair", "Other")
                        for (i in categories.chunked(2)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                i.forEach { category ->
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        val icon = when (category) {
                                            "Fuel" -> Icons.Default.LocalGasStation
                                            "Insurance" -> Icons.Default.Security
                                            "Repair" -> Icons.Default.Build
                                            else -> Icons.Default.MoreHoriz
                                        }
                                        Icon(icon, null)
                                        Text("$category\n$${uiState.byCategory[category] ?: 0.0}")
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Recent", style = MaterialTheme.typography.titleMedium)
                        TextButton(onClick = {
                            // TODO: navigate to History screen
                        }) {
                            Text("See all")
                        }
                    }
                }

                items(uiState.recent) { expense ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                navController.navigate(Screen.Details.createRoute(expense.id))
                            },
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row {
                            val icon = when (expense.category) {
                                "Fuel" -> Icons.Default.LocalGasStation
                                "Insurance" -> Icons.Default.Security
                                "Repair" -> Icons.Default.Build
                                else -> Icons.Default.MoreHoriz
                            }
                            Icon(icon, null)
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(expense.title ?: expense.category)
                                Text(expense.date, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Text("$${expense.amount}")
                    }
                }

                item {
                    Text("Planned", style = MaterialTheme.typography.titleMedium)
                }
                items(uiState.planned) { expense ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                navController.navigate(Screen.Details.createRoute(expense.id))
                            },
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row {
                            Icon(Icons.Default.Event, null)
                            Spacer(Modifier.width(8.dp))
                            Column {
                                val today = Clock.System.now()
                                    .toLocalDateTime(TimeZone.currentSystemDefault()).date
                                val expenseDate = LocalDate.parse(expense.date)
                                val daysLeft =
                                    (expenseDate.toEpochDays() - today.toEpochDays())

                                Text("${expense.title ?: expense.category} (in $daysLeft days)")
                                Text(expense.date, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Text("$${expense.amount}")
                    }
                }
            }
        }
    }
}
