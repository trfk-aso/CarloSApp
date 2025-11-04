package org.app.carlos.screens.addEdit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import carlosapp.composeapp.generated.resources.Res
import carlosapp.composeapp.generated.resources.bg_default
import carlosapp.composeapp.generated.resources.bg_marine
import carlosapp.composeapp.generated.resources.bg_midnight
import carlosapp.composeapp.generated.resources.bg_solaris
import org.app.carlos.screens.calenda.CalendarView
import org.app.carlos.viewModel.AddEditExpenseViewModel
import org.app.carlos.viewModel.FavoritesViewModel
import org.app.carlos.viewModel.HistoryViewModel
import org.app.carlos.viewModel.HomeViewModel
import org.app.carlos.viewModel.SearchViewModel
import org.app.carlos.viewModel.SettingsViewModel
import org.app.carlos.viewModel.StatisticsViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwitchDefaults
import androidx.compose.ui.graphics.SolidColor

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
    settingsViewModel: SettingsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val settingsUiState by settingsViewModel.uiState.collectAsState()
    var showCalendar by remember { mutableStateOf(false) }
    val selectedTheme = settingsUiState.themes.firstOrNull { it.isSelected }

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
        "default" -> Color(0xFFFDDB2C)
        "midnight" -> Color(0xFFB421FF)
        "solaris" -> Color(0xFFFFC654)
        "marine" -> Color(0xFF37FFE6)
        else -> Color(0xFFFDDB2C)
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
                    title = { Text(if (uiState.id == null) "New Expense" else "Edit Expense", color = textColor, fontWeight = FontWeight.SemiBold, fontSize = 20.sp) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = textColor)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.weight(1f),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = textColor)
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                        enabled = uiState.amount > 0,
                        modifier = Modifier.weight(1f),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (selectedTheme?.id) {
                                "default" -> Color(0xFFFFF315)
                                "midnight" -> Color(0xFFB421FF)
                                "solaris" -> Color(0xFFFFC654)
                                "marine" -> Color(0xFF37FFE6)
                                else -> Color(0xFFFFF315)
                            }
                        )
                    ) {
                        Text(
                            "Save",
                            color = if (selectedTheme?.id == "solaris" || selectedTheme?.id == "default") Color.Black else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Filters", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = textColor)
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    listOf("Fuel", "Insurance", "Repair", "Other").forEach { cat ->
                        FilterChip(
                            selected = uiState.category == cat,
                            onClick = { viewModel.updateCategory(cat) },
                            label = {
                                Text(
                                    cat,
                                    color = if (uiState.category == cat) {
                                        if (selectedTheme?.id == "solaris" || selectedTheme?.id == "marine" || selectedTheme?.id == "default") Color.Black else Color.White
                                    } else textColor
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = saveButtonColor,
                                containerColor = Color.Transparent
                            ),
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier
                                .height(36.dp)
                                .padding(horizontal = 4.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = if (uiState.category == "Fuel") "Amount (${settingsUiState.fuelUnit})" else "Amount",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textColor,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    OutlinedTextField(
                        value = if (uiState.amount == 0.0) "" else uiState.amount.toString(),
                        onValueChange = { viewModel.updateAmount(it) },
                        placeholder = { Text("$0.00", color = textColor.copy(alpha = 0.5f), fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                        textStyle = LocalTextStyle.current.copy(color = textColor, fontWeight = FontWeight.Bold, fontSize = 22.sp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = uiState.amount <= 0,
                        singleLine = true,
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCalendar = !showCalendar }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Date",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = textColor
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            uiState.date.toString(),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                            color = textColor
                        )
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = "Select date",
                            tint = textColor,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(24.dp)
                        )
                    }
                }

                if (showCalendar) {
                    CalendarView(
                        selectedDate = uiState.date,
                        onDateSelected = { picked ->
                            viewModel.updateDate(picked)
                            showCalendar = false
                        },
                        selectedThemeId = selectedTheme?.id
                    )
                }

                var isEditingTitle by remember { mutableStateOf(false) }
                var titleText by remember { mutableStateOf(uiState.title ?: "") }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isEditingTitle = true }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Title",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = textColor,
                        modifier = Modifier.width(120.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp)
                    ) {
                        if (isEditingTitle) {
                            BasicTextField(
                                value = titleText,
                                onValueChange = {
                                    titleText = it
                                    viewModel.updateTitle(it)
                                },
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(
                                    color = textColor,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 18.sp
                                ),
                                cursorBrush = SolidColor(textColor),
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text(
                                text = if (titleText.isEmpty()) "Optional title" else titleText,
                                color = if (titleText.isEmpty()) textColor.copy(alpha = 0.5f) else textColor,
                                fontWeight = FontWeight.Normal,
                                fontSize = 18.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                var isEditingNotes by remember { mutableStateOf(false) }
                var notesText by remember { mutableStateOf(uiState.notes ?: "") }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isEditingNotes = true }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        "Notes",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = textColor,
                        modifier = Modifier.width(120.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp),
                        contentAlignment = Alignment.TopStart
                    ) {
                        if (isEditingNotes) {
                            BasicTextField(
                                value = notesText,
                                onValueChange = {
                                    notesText = it
                                    viewModel.updateNotes(it)
                                },
                                textStyle = LocalTextStyle.current.copy(
                                    color = textColor,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 18.sp
                                ),
                                cursorBrush = SolidColor(textColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .padding(end = 4.dp),
                                maxLines = 6
                            )
                        } else {
                            Text(
                                text = if (notesText.isEmpty()) "Optional notes" else notesText,
                                color = if (notesText.isEmpty()) textColor.copy(alpha = 0.5f) else textColor,
                                fontWeight = FontWeight.Normal,
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp),
                                maxLines = 6
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Save as Template",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textColor
                    )
                    Switch(
                        checked = uiState.isFavoriteTemplate,
                        onCheckedChange = { viewModel.updateTemplate(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            uncheckedThumbColor = Color.Gray,
                            checkedTrackColor = Color(0xFF3DF300),
                            uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.height(32.dp)
                    )
                }
            }
        }
    }
}