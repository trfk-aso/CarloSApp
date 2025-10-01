package org.app.carlos.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.datetime.LocalDate
import org.app.carlos.exporter.FileExporter
import org.app.carlos.exporter.provideFileExporter
import org.app.carlos.screens.Screen
import org.app.carlos.screens.bottom.BottomNavBar
import org.app.carlos.screens.calenda.CalendarView
import org.app.carlos.screens.list.SwipeToDismissItem
import org.app.carlos.screens.list.formatAmount
import org.app.carlos.viewModel.HistoryViewModel
import org.app.carlos.viewModel.HomeViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavHostController,
    viewModel: HistoryViewModel = koinInject(),
    homeViewModel: HomeViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    var showFilters by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategories by remember { mutableStateOf(setOf<String>()) }
    var dateFrom by remember { mutableStateOf<LocalDate?>(null) }
    var dateTo by remember { mutableStateOf<LocalDate?>(null) }
    var amountMin by remember { mutableStateOf("") }
    var amountMax by remember { mutableStateOf("") }

    val categories = listOf("Fuel", "Insurance", "Repair", "Other")
    var showFromCalendar by remember { mutableStateOf(false) }
    var showToCalendar by remember { mutableStateOf(false) }

    val fileExporter = provideFileExporter()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E3A8A)),
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filters", tint = Color.White)
                    }
                    TextButton(onClick = { viewModel.exportHistory(fileExporter) }) {
                        Text("Export", color = Color.White)
                    }
                }
            )
        },
        bottomBar = { BottomNavBar(navController, currentRoute) },
        containerColor = Color(0xFF1E3A8A)
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            if (showFilters) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search by title or notes") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear")
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Categories", fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            categories.forEach { category ->
                                FilterChip(
                                    selected = selectedCategories.contains(category),
                                    onClick = {
                                        selectedCategories = if (selectedCategories.contains(category)) {
                                            selectedCategories - category
                                        } else selectedCategories + category
                                    },
                                    label = { Text(category) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Text("Date range", fontWeight = FontWeight.SemiBold)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("From:", modifier = Modifier.width(80.dp))
                                OutlinedButton(
                                    onClick = { showFromCalendar = !showFromCalendar },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(dateFrom?.toString() ?: "Select date")
                                    Spacer(Modifier.weight(1f))
                                    Icon(Icons.Default.CalendarToday, contentDescription = "Pick date")
                                }
                            }
                            if (showFromCalendar) {
                                CalendarView(selectedDate = dateFrom) {
                                    dateFrom = it
                                    showFromCalendar = false
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("To:", modifier = Modifier.width(80.dp))
                                OutlinedButton(
                                    onClick = { showToCalendar = !showToCalendar },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(dateTo?.toString() ?: "Select date")
                                    Spacer(Modifier.weight(1f))
                                    Icon(Icons.Default.CalendarToday, contentDescription = "Pick date")
                                }
                            }
                            if (showToCalendar) {
                                CalendarView(selectedDate = dateTo) {
                                    dateTo = it
                                    showToCalendar = false
                                }
                            }
                        }

                        Text("Amount", fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Text("Min:", modifier = Modifier.width(50.dp))
                                OutlinedTextField(
                                    value = amountMin,
                                    onValueChange = { amountMin = it.filter { c -> c.isDigit() || c == '.' } },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Text("Max:", modifier = Modifier.width(50.dp))
                                OutlinedTextField(
                                    value = amountMax,
                                    onValueChange = { amountMax = it.filter { c -> c.isDigit() || c == '.' } },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = {
                                    searchQuery = ""
                                    selectedCategories = emptySet()
                                    dateFrom = null
                                    dateTo = null
                                    amountMin = ""
                                    amountMax = ""
                                    viewModel.applyFilters()
                                },
                                modifier = Modifier.weight(1f)
                            ) { Text("Reset filters") }

                            Button(
                                onClick = {
                                    viewModel.applyFilters(
                                        query = searchQuery,
                                        categories = selectedCategories,
                                        dateFrom = dateFrom,
                                        dateTo = dateTo,
                                        amountMin = amountMin.toDoubleOrNull(),
                                        amountMax = amountMax.toDoubleOrNull()
                                    )
                                    showFilters = false
                                },
                                modifier = Modifier.weight(1f)
                            ) { Text("Apply") }
                        }
                    }
                }
            }

            if (uiState.isEmpty) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = Color(0xFF93C5FD),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("No history recorded yet.", color = Color(0xFF93C5FD))
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .background(Color(0xFF1E3A8A))
                ) {
                    uiState.groups.forEach { (ym, expenses) ->
                        stickyHeader {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF1E3A8A))
                                    .padding(12.dp)
                            ) {
                                val total = uiState.totals[ym] ?: 0.0
                                Text(
                                    "${ym} — ${formatAmount(total)}",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                        items(expenses, key = { it.id!! }) { exp ->
                            SwipeToDismissItem(
                                expense = exp,
                                navController = navController,
                                onDelete = {
                                    exp.id?.let {
                                        viewModel.deleteExpense(it)
                                        homeViewModel.refreshData()
                                    }
                                }
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}