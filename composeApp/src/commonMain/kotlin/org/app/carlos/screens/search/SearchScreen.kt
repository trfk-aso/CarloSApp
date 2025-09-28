package org.app.carlos.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.app.carlos.screens.Screen
import org.app.carlos.screens.bottom.BottomNavBar
import org.app.carlos.screens.calenda.CalendarView
import org.app.carlos.viewModel.HomeViewModel
import org.app.carlos.viewModel.SearchViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavHostController,
    searchViewModel: SearchViewModel = koinInject()
) {
    val uiState by searchViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategories by remember { mutableStateOf(setOf<String>()) }
    var dateFrom by remember { mutableStateOf<LocalDate?>(null) }
    var dateTo by remember { mutableStateOf<LocalDate?>(null) }
    var amountMin by remember { mutableStateOf("") }
    var amountMax by remember { mutableStateOf("") }

    var showFromCalendar by remember { mutableStateOf(false) }
    var showToCalendar by remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    val categories = listOf("Fuel", "Insurance", "Repair", "Other")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search", fontWeight = FontWeight.Bold) }
            )
        },
        bottomBar = {
            Column {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Button(
                        onClick = {
                            searchQuery = ""
                            selectedCategories = emptySet()
                            dateFrom = null
                            dateTo = null
                            amountMin = ""
                            amountMax = ""
                            searchViewModel.search(null, emptySet(), null, null, "", "")
                        },
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.LightGray),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text("Reset filters")
                    }

                    Spacer(Modifier.width(8.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                searchViewModel.search(
                                    query = searchQuery,
                                    categories = selectedCategories,
                                    dateFrom = dateFrom,
                                    dateTo = dateTo,
                                    amountMin = amountMin,
                                    amountMax = amountMax
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text("Apply")
                    }
                }

                BottomNavBar(navController, currentRoute)
            }
        },
        content = { paddingValues ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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

                Text("Filters", fontWeight = FontWeight.SemiBold)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    categories.forEach { category ->
                        FilterChip(
                            selected = selectedCategories.contains(category),
                            onClick = {
                                selectedCategories = if (selectedCategories.contains(category)) {
                                    selectedCategories - category
                                } else {
                                    selectedCategories + category
                                }
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Min:", modifier = Modifier.width(50.dp))
                        OutlinedTextField(
                            value = amountMin,
                            onValueChange = { amountMin = it.filter { c -> c.isDigit() || c == '.' } },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Max:", modifier = Modifier.width(50.dp))
                        OutlinedTextField(
                            value = amountMax,
                            onValueChange = { amountMax = it.filter { c -> c.isDigit() || c == '.' } },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Text("Recent searches", fontWeight = FontWeight.SemiBold)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    uiState.recentSearches.forEach { recent ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable {
                                    searchQuery = recent
                                    searchViewModel.search(
                                        query = recent,
                                        categories = selectedCategories,
                                        dateFrom = dateFrom,
                                        dateTo = dateTo,
                                        amountMin = amountMin,
                                        amountMax = amountMax
                                    )
                                }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(recent)
                        }
                    }
                }

                if (uiState.isEmpty) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 50.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No expenses found")
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = {
                            searchQuery = ""
                            selectedCategories = emptySet()
                            dateFrom = null
                            dateTo = null
                            amountMin = ""
                            amountMax = ""
                            searchViewModel.search(null, emptySet(), null, null, "", "")
                        }) {
                            Text("Clear filters")
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        uiState.results.forEach { expense ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.AttachMoney, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("${expense.title} - ${expense.amount}")
                            }
                        }
                    }
                }
            }
        }
    )
}