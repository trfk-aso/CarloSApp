package org.app.carlos.screens.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.app.carlos.screens.Screen
import org.app.carlos.viewModel.ExpenseDetailsViewModel
import org.app.carlos.viewModel.HomeViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseDetailsScreen(
    navController: NavHostController,
    viewModel: ExpenseDetailsViewModel = koinInject(),
    homeViewModel: HomeViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    val refreshId = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<Long?>("refreshExpenseId", null)
        ?.collectAsState()

    LaunchedEffect(refreshId?.value) {
        refreshId?.value?.let { id ->
            viewModel.loadExpense(id)
            navController.currentBackStackEntry?.savedStateHandle?.set("refreshExpenseId", null)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expense Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val idToEdit = uiState.id
                        println("Editing expense, id from UI state: $idToEdit")

                        navController.navigate(Screen.AddEditExpense.createRoute(idToEdit)) {
                            launchSingleTop = true
                        }

                        println("Navigated to: ${Screen.AddEditExpense.createRoute(idToEdit)}")
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            DetailRow("Amount", "$${uiState.amount}", onClick = { /* edit amount */ }, withArrow = true)
            DetailRow("Category", uiState.category)
            DetailRow("Date", uiState.date)
            DetailRow("Title", uiState.title.ifBlank { "—" })
            DetailRow("Notes", uiState.notes.ifBlank { "—" })

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Save as Template")
                Switch(
                    checked = uiState.isTemplate,
                    onCheckedChange = { viewModel.toggleTemplate(it) }
                )
            }

            if (uiState.isTemplate) {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.saveTemplate() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
                ) {
                    Text("Save Template")
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Delete Expense")
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete expense?") },
                text = { Text("The expense will be permanently deleted.") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteExpense()
                        homeViewModel.refreshData()
                        showDeleteDialog = false
                        navController.popBackStack()
                    }) {
                        Text("Delete", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                },
                modifier = Modifier.clip(RoundedCornerShape(16.dp))
            )
        }
    }
}

@Composable
fun DetailRow(label: String, value: String, onClick: (() -> Unit)? = null, withArrow: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(value, style = MaterialTheme.typography.bodyMedium)
            if (withArrow) {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
            }
        }
    }
    Divider()
}
