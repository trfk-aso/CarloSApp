package org.app.carlos.screens.addEdit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.app.carlos.screens.calenda.CalendarView
import org.app.carlos.viewModel.AddEditExpenseViewModel
import org.app.carlos.viewModel.FavoritesViewModel
import org.app.carlos.viewModel.HistoryViewModel
import org.app.carlos.viewModel.HomeViewModel
import org.app.carlos.viewModel.SearchViewModel
import org.app.carlos.viewModel.SettingsViewModel
import org.app.carlos.viewModel.StatisticsViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditExpenseScreen(
    navController: NavController,
    viewModel: AddEditExpenseViewModel = koinInject(),
    homeViewModel: HomeViewModel = koinInject(),
    searchViewModel: SearchViewModel = koinInject(),
    favoritesViewModel: FavoritesViewModel = koinInject(),
    historyViewModel: HistoryViewModel = koinInject(),
    statisticsViewModel: StatisticsViewModel = koinInject(),
    settingsViewModel: SettingsViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val settingsUiState by settingsViewModel.uiState.collectAsState()
    var showCalendar by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.id) {
        println("Current expense id: ${uiState.id}")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.id == null) "New Expense" else "Edit Expense") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = { navController.popBackStack() }) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        viewModel.saveExpense()
                        homeViewModel.refreshData()
                        searchViewModel.search()
                        favoritesViewModel.loadFavorites()
                        historyViewModel.refresh()
                        statisticsViewModel.refreshData()
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("refreshExpenseId", uiState.id)
                        navController.popBackStack()
                    },
                    enabled = uiState.amount > 0
                ) {
                    Text("Save")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Fuel", "Insurance", "Repair", "Other").forEach { cat ->
                    FilterChip(
                        selected = uiState.category == cat,
                        onClick = { viewModel.updateCategory(cat) },
                        label = { Text(cat) }
                    )
                }
            }

            Column {
                Text(
                    text = if (uiState.category == "Fuel") {
                        "Amount (${settingsUiState.fuelUnit})"
                    } else {
                        "Amount"
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                OutlinedTextField(
                    value = if (uiState.amount == 0.0) "" else uiState.amount.toString(),
                    onValueChange = { viewModel.updateAmount(it) },
                    placeholder = { Text("$0.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = uiState.amount <= 0,
                    modifier = Modifier.fillMaxWidth()
                )

                if (uiState.amount <= 0) {
                    Text(
                        "Enter a valid amount",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Column {
                Text("Date", style = MaterialTheme.typography.labelMedium)
                OutlinedButton(
                    onClick = { showCalendar = !showCalendar },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(uiState.date.toString())
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Default.ArrowForward, contentDescription = "Select date")
                }

                if (showCalendar) {
                    CalendarView(
                        selectedDate = uiState.date,
                        onDateSelected = { picked ->
                            viewModel.updateDate(picked)
                            showCalendar = false
                        }
                    )
                }
            }

            OutlinedTextField(
                value = uiState.title ?: "",
                onValueChange = { viewModel.updateTitle(it) },
                label = { Text("Title") },
                placeholder = { Text("Optional title") }
            )

            OutlinedTextField(
                value = uiState.notes ?: "",
                onValueChange = { viewModel.updateNotes(it) },
                label = { Text("Notes") },
                placeholder = { Text("Optional notes") },
                modifier = Modifier.height(120.dp),
                maxLines = 4
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Save as Template")
                Switch(
                    checked = uiState.isFavoriteTemplate,
                    onCheckedChange = { viewModel.updateTemplate(it) }
                )
            }
        }
    }
}