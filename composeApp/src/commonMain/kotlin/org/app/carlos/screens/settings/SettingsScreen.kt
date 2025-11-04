package org.app.carlos.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import carlosapp.composeapp.generated.resources.Res
import carlosapp.composeapp.generated.resources.bg_default
import carlosapp.composeapp.generated.resources.bg_marine
import carlosapp.composeapp.generated.resources.bg_midnight
import carlosapp.composeapp.generated.resources.bg_solaris
import org.app.carlos.data.repository.ThemeRepository
import org.app.carlos.exporter.provideFileExporter
import org.app.carlos.screens.Screen
import org.app.carlos.viewModel.FavoritesViewModel
import org.app.carlos.viewModel.HistoryViewModel
import org.app.carlos.viewModel.HomeViewModel
import org.app.carlos.viewModel.SearchViewModel
import org.app.carlos.viewModel.SettingsViewModel
import org.app.carlos.viewModel.StatisticsViewModel
import org.jetbrains.compose.resources.painterResource
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

    val selectedTheme = uiState.themes.firstOrNull { it.isSelected }
    val backgroundRes = when (selectedTheme?.id) {
        "default" -> Res.drawable.bg_default
        "midnight" -> Res.drawable.bg_midnight
        "solaris" -> Res.drawable.bg_solaris
        "marine" -> Res.drawable.bg_marine
        else -> Res.drawable.bg_default
    }
    val rowBackgroundFuel = when (selectedTheme?.id) {
        "default" -> Color(0xFF1B2D8A)
        "midnight" -> Color(0xFF1D1B49)
        "solaris" -> Color(0xFFFFE8A5)
        "marine" -> Color(0xFF22272E)
        else -> Color(0xFF1E3A8A)
    }

    val text = when (selectedTheme?.id) {
        "default" -> Color.White
        "midnight" -> Color.White
        "solaris" -> Color.Black
        "marine" -> Color.White
        else -> Color.White
    }

    val export = when (selectedTheme?.name?.lowercase()) {
        "default" -> Color.White
        "midnight" -> Color(0xFFFF08D8)
        "solaris" -> Color.Black
        "marine" -> Color(0xFF12FAFF)
        else -> Color.White
    }

    val fileExporter = provideFileExporter()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(backgroundRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        val overlayAlpha = if (selectedTheme?.id == "solaris") 0f else 0.4f
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = overlayAlpha))
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Settings", color = text) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = text
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(start = 12.dp, end = 12.dp, top = 32.dp, bottom = 12.dp)
            ) {
                item {
                    Text("Themes", color = text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))

                    val rowBackground = when (selectedTheme?.name?.lowercase()) {
                        "default" -> Color(0xFF1B2D8A)
                        "midnight" -> Color(0xFF1D1B49)
                        "solaris" -> Color(0xFFFFE8A5)
                        "marine" -> Color(0xFF22272E)
                        else -> Color(0xFF1E3A8A)
                    }

                    val buttonBackground = when (selectedTheme?.name?.lowercase()) {
                        "default" -> Color(0xFFFFE100)
                        "midnight" -> Color(0xFFFF08D8)
                        "solaris" -> Color(0xFFFFC654)
                        "marine" -> Color(0xFF12FAFF)
                        else -> Color(0xFFFACC15)
                    }

                    uiState.themes.forEach { theme ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp)
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = rowBackground),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(theme.name, color = text)

                                when {
                                    theme.isSelected -> {
                                        Text(
                                            text = "Selected",
                                            color = text,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    theme.isPurchased -> {
                                        ThemeActionButton(
                                            text = "Use",
                                            background = buttonBackground,
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
                                            background = buttonBackground,
                                            textColor = Color.Black
                                        ) {
                                            viewModel.showUnlockDialog(theme.id)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { viewModel.restorePurchases() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(56.dp),
                        border = BorderStroke(1.dp, buttonBackground),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = buttonBackground)
                    ) {
                        Text("Restore Purchases", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = buttonBackground)
                    }

                    Spacer(Modifier.height(16.dp))
                }

                item {
                    Text(
                        "Preferences",
                        color = text,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Fuel Unit", color = text)

                        Spacer(Modifier.width(24.dp))

                        FuelUnitSelector(
                            options = listOf("Liters", "Gallons"),
                            selectedIndex = if (uiState.fuelUnit == "Liters") 0 else 1,
                            onSelect = { index ->
                                viewModel.setFuelUnit(if (index == 0) "Liters" else "Gallons")
                            },
                            rowBackground = rowBackgroundFuel
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                }

                item {
                    Spacer(Modifier.height(8.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate(Screen.About.route) }
                    ) {
                        Divider(
                            color = Color.Gray,
                            thickness = 1.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )

                        Spacer(Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "About",
                                color = text,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = text
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        Divider(
                            color = Color.Gray,
                            thickness = 1.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))
                }

                item {
                    Text(
                        "Data",
                        color = text,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { historyViewModel.exportHistory(fileExporter) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(56.dp),
                        border = BorderStroke(1.dp, Color.White),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = export)
                    ) {
                        Text("Export all expenses", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.showResetDialog() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Reset All Data", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }

                    Spacer(Modifier.height(20.dp))
                }
            }
        }

        val unlockDialogBackground = when (selectedTheme?.id) {
            "default" -> Color(0xFF22232B)
            "midnight" -> Color(0xFF22232B)
            "solaris" -> Color.White
            "marine" -> Color(0xFF22232B)
            else -> Color(0xFF22232B)
        }

        val buyButtonColor = when (selectedTheme?.id) {
            "default" -> Color(0xFFFFE100)
            "midnight" -> Color(0xFFFF08D8)
            "solaris" -> Color(0xFFFFC654)
            "marine" -> Color(0xFF23D7FF)
            else -> Color(0xFF9B5EFF)
        }

        if (uiState.showResetDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.hideResetDialog() },
                title = {
                    Text(
                        "Reset all data?",
                        color = text
                    )
                },
                text = {
                    Text(
                        "This will permanently delete all expenses and templates.",
                        color = text
                    )
                },
                confirmButton = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.hideResetDialog() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF9E9E9E),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                        ) {
                            Text("Cancel")
                        }

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
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                        ) {
                            Text("Reset")
                        }
                    }
                },
                containerColor = unlockDialogBackground
            )
        }

        if (uiState.showUnlockDialog != null) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissUnlockDialog() },
                title = {
                    Text(
                        "Unlock theme?",
                        color = text
                    )
                },
                text = {
                    Text(
                        "To use this theme, you need to unlock it first.",
                        color = text
                    )
                },
                confirmButton = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.dismissUnlockDialog() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF9E9E9E),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = { viewModel.buyTheme(uiState.showUnlockDialog!!) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = buyButtonColor,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                        ) {
                            Text("Buy")
                        }
                    }
                },
                containerColor = unlockDialogBackground
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
        modifier = Modifier
            .width(120.dp)
            .height(40.dp),
        colors = ButtonDefaults.buttonColors(containerColor = background),
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun FuelUnitSelector(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    rowBackground: Color
) {
    Row(
        modifier = Modifier
            .background(Color.Transparent, RoundedCornerShape(8.dp))
            .padding(2.dp)
            .height(32.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        options.forEachIndexed { index, option ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
                    .background(
                        if (isSelected) Color.White else rowBackground,
                        RoundedCornerShape(6.dp)
                    )
                    .clickable { onSelect(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    option,
                    color = if (isSelected) Color.Black else Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

fun getThemeButtonColors(themeName: String, state: String): Pair<Color, Color> {
    val backgroundColor = when (themeName.lowercase()) {
        "default" -> Color(0xFF1B2D8A)
        "midnight" -> Color(0xFF1D1B49)
        "solaris" -> Color(0xFFFFE8A5)
        "marine" -> Color(0xFF22272E)
        else -> Color.Gray
    }

    val textColor = when (state) {
        "selected" -> Color.White
        "use" -> Color.Black
        "buy" -> Color.Black
        else -> Color.White
    }

    return Pair(backgroundColor, textColor)
}


