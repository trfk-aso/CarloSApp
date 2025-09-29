package org.app.carlos.screens.list

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.app.carlos.data.model.Expense
import org.app.carlos.screens.Screen
import org.app.carlos.viewModel.ListViewModel
import org.app.carlos.viewModel.SearchViewModel
import org.koin.compose.koinInject
import androidx.compose.material.*
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.app.carlos.screens.calenda.CalendarView
import org.app.carlos.viewModel.HomeViewModel
import org.app.carlos.viewModel.SortOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    navController: NavHostController,
    searchViewModel: SearchViewModel = koinInject(),
    homeViewModel: HomeViewModel = koinInject()
) {
    val uiState by searchViewModel.uiState.collectAsState()
    var showSortMenu by remember { mutableStateOf(false) }
    var showFilters by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategories by remember { mutableStateOf(setOf<String>()) }
    var dateFrom by remember { mutableStateOf<LocalDate?>(null) }
    var dateTo by remember { mutableStateOf<LocalDate?>(null) }
    var amountMin by remember { mutableStateOf("") }
    var amountMax by remember { mutableStateOf("") }

    var showFromCalendar by remember { mutableStateOf(false) }
    var showToCalendar by remember { mutableStateOf(false) }

    val categories = listOf("Fuel", "Insurance", "Repair", "Other")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.title, fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filters", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E3A8A))
            )
        },
        containerColor = Color(0xFF1E3A8A)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = showSortMenu,
                onExpandedChange = { showSortMenu = !showSortMenu },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = uiState.sortOrder.label,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showSortMenu) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF0F2E6D),
                        unfocusedContainerColor = Color(0xFF0F2E6D),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
                ExposedDropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    SortOrder.values().forEach { order ->
                        DropdownMenuItem(
                            text = { Text(order.label) },
                            onClick = {
                                searchViewModel.changeSort(order)
                                showSortMenu = false
                            }
                        )
                    }
                }
            }

            if (showSortMenu) {
                Spacer(modifier = Modifier.height(200.dp))
            }

            if (showFilters) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
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
                                        selectedCategories =
                                            if (selectedCategories.contains(category)) {
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
                                    Icon(
                                        Icons.Default.CalendarToday,
                                        contentDescription = "Pick date"
                                    )
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
                                    Icon(
                                        Icons.Default.CalendarToday,
                                        contentDescription = "Pick date"
                                    )
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
                                    onValueChange = {
                                        amountMin = it.filter { c -> c.isDigit() || c == '.' }
                                    },
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
                                    onValueChange = {
                                        amountMax = it.filter { c -> c.isDigit() || c == '.' }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                                    showFilters = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFFFFD700
                                    )
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                            ) {
                                Text("Apply")
                            }
                        }
                    }
                }
            }

            if (uiState.isEmpty) {
                EmptyListState(onClearFilters = {
                    searchQuery = ""
                    selectedCategories = emptySet()
                    dateFrom = null
                    dateTo = null
                    amountMin = ""
                    amountMax = ""
                    searchViewModel.search(null, emptySet(), null, null, "", "")
                })
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.results, key = { it.id!! }) { expense ->
                        SwipeToDismissItem(
                            expense = expense,
                            navController = navController,
                            onDelete = { searchViewModel.confirmDelete(expense) }
                        )
                    }
                }
            }
        }

        if (uiState.showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { searchViewModel.dismissDeleteDialog() },
                title = { Text("Delete expense?") },
                text = { Text("This action cannot be undone.") },
                confirmButton = {
                    TextButton(onClick = {
                        searchViewModel.deleteConfirmed()
                        homeViewModel.refreshData()
                        searchViewModel.search()
                    }) {
                        Text("Delete", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { searchViewModel.dismissDeleteDialog() }) {
                        Text("Cancel")
                    }
                },
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}