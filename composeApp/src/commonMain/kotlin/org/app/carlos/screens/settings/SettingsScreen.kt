package org.app.carlos.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import org.app.carlos.data.repository.ThemeRepository
import org.app.carlos.exporter.provideFileExporter
import org.app.carlos.screens.Screen
import org.app.carlos.viewModel.FavoritesViewModel
import org.app.carlos.viewModel.HistoryViewModel
import org.app.carlos.viewModel.HomeViewModel
import org.app.carlos.viewModel.SearchViewModel
import org.app.carlos.viewModel.SettingsViewModel
import org.app.carlos.viewModel.StatisticsViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel,
    themeRepository: ThemeRepository,
    historyViewModel: HistoryViewModel = koinInject(),
    homeViewModel: HomeViewModel = koinInject(),
    searchViewModel: SearchViewModel = koinInject(),
    favoritesViewModel: FavoritesViewModel = koinInject(),
    statisticsViewModel: StatisticsViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    val fileExporter = provideFileExporter()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color(0xFF0F172A)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF0F172A))
                .padding(start = 12.dp, end = 12.dp, top = 32.dp, bottom = 12.dp)
        ) {
            item {
                Text("Themes", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))

                uiState.themes.forEach { theme ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (theme.isSelected) Color(0xFF1E3A8A) else Color(
                                0xFF1E3A8A
                            )
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(theme.name, color = Color.White)

                            when {
                                theme.isSelected -> {
                                    Text("Selected", color = Color.White)
                                }

                                theme.isPurchased -> {
                                    ThemeActionButton(
                                        text = "Use",
                                        background = Color(0xFFFACC15),
                                        textColor = Color.Black
                                    ) { viewModel.useTheme(theme.id) }
                                }

                                theme.isLoading -> {
                                    CircularProgressIndicator(
                                        color = Color.Gray,
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                }

                                theme.hasError -> {
                                    Text(
                                        "Purchase failed, please try again.",
                                        color = Color.Red,
                                        fontSize = 12.sp
                                    )
                                }

                                else -> {
                                    ThemeActionButton(
                                        text = "${theme.price} Buy",
                                        background = Color(0xFFFACC15),
                                        textColor = Color.Black
                                    ) { viewModel.buyTheme(theme.id) }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { viewModel.restorePurchases() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Yellow),
                    border = BorderStroke(1.dp, Color.Yellow)
                ) {
                    Text("Restore Purchases", color = Color.Yellow)
                }

                Spacer(Modifier.height(16.dp))
            }

            item {
                Text(
                    "Preferences",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Fuel Unit", color = Color.White)
                    FuelUnitSelector(
                        options = listOf("Liters", "Gallons"),
                        selectedIndex = if (uiState.fuelUnit == "Liters") 0 else 1
                    ) { index ->
                        viewModel.setFuelUnit(if (index == 0) "Liters" else "Gallons")
                    }
                }

                Spacer(Modifier.height(16.dp))
            }

            item {
                Text("About", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate(Screen.About.route) },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E3A8A))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("About app", color = Color.White)
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            } 

            item {
                Text("Data", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { historyViewModel.exportHistory(fileExporter) },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, Color.White),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("Export all expenses")
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = { viewModel.showResetDialog() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Reset All Data", color = Color.White)
                }

                Spacer(Modifier.height(16.dp))
            }
        }

        if (uiState.showResetDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.hideResetDialog() },
                title = { Text("Reset all data?", color = Color.White) },
                text = {
                    Text(
                        "This will permanently delete all expenses and templates.",
                        color = Color.White
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.resetAllData()
                            homeViewModel.refreshData()
                            searchViewModel.search()
                            searchViewModel.clearRecentSearches()
                            favoritesViewModel.loadFavorites()
                            historyViewModel.refresh()
                            statisticsViewModel.refreshData()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Reset", color = Color.White)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { viewModel.hideResetDialog() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Cancel", color = Color.White)
                    }
                },
                containerColor = Color(0xFF1E3A8A)
            )
        }

        if (uiState.showUnlockDialog != null) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissUnlockDialog() },
                title = { Text("Unlock theme?", color = Color.White) },
                text = {
                    Text(
                        "To use this theme, you need to unlock it first.",
                        color = Color.White
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.buyTheme(uiState.showUnlockDialog!!) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFACC15))
                    ) {
                        Text("Buy", color = Color.Black)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { viewModel.dismissUnlockDialog() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Cancel", color = Color.White)
                    }
                },
                containerColor = Color(0xFF1E3A8A)
            )
        }
    }
}

@Composable
fun ThemeActionButton(
    text: String,
    background: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = background),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(text, color = textColor)
    }
}

@Composable
fun FuelUnitSelector(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .background(Color(0xFF1E40AF), RoundedCornerShape(8.dp))
            .padding(2.dp)
            .height(40.dp)
    ) {
        options.forEachIndexed { index, option ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        if (isSelected) Color(0xFF2563EB) else Color.Transparent,
                        RoundedCornerShape(6.dp)
                    )
                    .clickable { onSelect(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(option, color = Color.White)
            }
        }
    }
}

