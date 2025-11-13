package org.app.carlos.screens.details

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import org.app.carlos.screens.Screen
import org.app.carlos.viewModel.ExpenseDetailsViewModel
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
fun ExpenseDetailsScreen(
    navController: NavHostController,
    viewModel: ExpenseDetailsViewModel = koinInject(),
    homeViewModel: HomeViewModel = koinInject(),
    searchViewModel: SearchViewModel = koinInject(),
    favoritesViewModel: FavoritesViewModel = koinInject(),
    historyViewModel: HistoryViewModel = koinInject(),
    statisticsViewModel: StatisticsViewModel = koinInject(),
    settingsViewModel: SettingsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val settingsUiState by settingsViewModel.uiState.collectAsState()
    val selectedTheme = settingsUiState.themes.firstOrNull { it.isSelected }

    var showDeleteDialog by remember { mutableStateOf(false) }

    val backgroundRes = when (selectedTheme?.id) {
        "default" -> Res.drawable.bg_default
        "midnight" -> Res.drawable.bg_midnight
        "solaris" -> Res.drawable.bg_solaris
        "marine" -> Res.drawable.bg_marine
        else -> Res.drawable.bg_default
    }
    val overlayAlpha = if (selectedTheme?.id == "solaris") 0f else 0.4f
    val textColor = when (selectedTheme?.id) {
        "default", "midnight", "marine" -> Color.White
        "solaris" -> Color.Black
        else -> Color.White
    }
    val saveButtonColor = when (selectedTheme?.id) {
        "default" -> Color(0xFFFFF315)
        "midnight" -> Color(0xFFB421FF)
        "solaris" -> Color(0xFFFFC654)
        "marine" -> Color(0xFF37FFE6)
        else -> Color(0xFFFFF315)
    }

    val text = when (selectedTheme?.id) {
        "default" -> Color.White
        "midnight" -> Color.White
        "solaris" -> Color.Black
        "marine" -> Color.White
        else -> Color.White
    }

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
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Expense Details",
                            color = textColor,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = textColor)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            val idToEdit = uiState.id ?: return@IconButton
                            navController.navigate("${Screen.AddEditExpense.route}?expenseId=$idToEdit") {
                                launchSingleTop = true
                            }
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = textColor)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = textColor,
                        navigationIconContentColor = textColor,
                        actionIconContentColor = textColor
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DetailRow("Amount", "$${uiState.amount}", withArrow = true, textColor = textColor)
                DetailRow("Category", uiState.category, textColor = textColor)
                DetailRow("Date", uiState.date, textColor = textColor)
                DetailRow("Title", uiState.title.ifBlank { "—" }, textColor = textColor)
                DetailRow("Notes", uiState.notes.ifBlank { "—" }, textColor = textColor)

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Save as Template",
                        color = textColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Switch(
                        checked = uiState.isTemplate,
                        onCheckedChange = { checked ->
                            viewModel.toggleTemplate(checked)
                            favoritesViewModel.loadFavorites()
                        },
                        modifier = Modifier.scale(1.3f),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            uncheckedThumbColor = Color.Gray,
                            checkedTrackColor = Color(0xFF3DF300),
                            uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
                        )
                    )
                }

                val templateButtonColor = when (selectedTheme?.id) {
                    "default" -> Color(0xFFFFF315)
                    "midnight" -> Color(0xFFB421FF)
                    "solaris" -> Color(0xFFFFC654)
                    "marine" -> Color(0xFF37FFE6)
                    else -> Color(0xFFFFF315)
                }

                val templateTextColor = when (selectedTheme?.id) {
                    "default" -> Color.Black
                    "midnight" -> Color.White
                    "solaris" -> Color.Black
                    "marine" -> Color.Black
                    else -> Color.Black
                }


                if (uiState.isTemplate) {
                    Spacer(Modifier.height(16.dp))
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val alpha by animateFloatAsState(if (isPressed) 0.7f else 1f)

                    Button(
                        onClick = {
                            viewModel.saveTemplate()
                            favoritesViewModel.loadFavorites()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .graphicsLayer(alpha = alpha),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = templateButtonColor),
                        interactionSource = interactionSource
                    ) {
                        Text(
                            "Save Template",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = templateTextColor
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                Button(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(
                        "Delete Expense",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            val unlockDialogBackground = when (selectedTheme?.id) {
                "default" -> Color(0xFF22232B)
                "midnight" -> Color(0xFF22232B)
                "solaris" -> Color.White
                "marine" -> Color(0xFF22232B)
                else -> Color(0xFF22232B)
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Delete expense?", color = text) },
                    text = { Text("The expense will be permanently deleted.", color = text) },
                    confirmButton = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { showDeleteDialog = false },
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
                                    viewModel.deleteExpense()
                                    homeViewModel.refreshData()
                                    showDeleteDialog = false
                                    navController.popBackStack()
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
                                Text("Delete")
                            }
                        }
                    },
                    containerColor = unlockDialogBackground,
                    modifier = Modifier.clip(RoundedCornerShape(16.dp))
                )
            }
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    onClick: (() -> Unit)? = null,
    withArrow: Boolean = false,
    textColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(vertical = 12.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor.copy(alpha = 0.8f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.height(6.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                value,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            if (withArrow) {
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(Modifier.height(6.dp))
        Divider(color = textColor.copy(alpha = 0.3f), thickness = 1.dp)
    }
}