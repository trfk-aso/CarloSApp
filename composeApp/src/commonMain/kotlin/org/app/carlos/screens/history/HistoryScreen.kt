package org.app.carlos.screens.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import carlosapp.composeapp.generated.resources.Res
import carlosapp.composeapp.generated.resources.bg_default
import carlosapp.composeapp.generated.resources.bg_marine
import carlosapp.composeapp.generated.resources.bg_midnight
import carlosapp.composeapp.generated.resources.bg_solaris
import carlosapp.composeapp.generated.resources.empty_default_history
import carlosapp.composeapp.generated.resources.empty_marine_history
import carlosapp.composeapp.generated.resources.empty_midnight_history
import carlosapp.composeapp.generated.resources.empty_solaris_history
import carlosapp.composeapp.generated.resources.ic_filter_default
import carlosapp.composeapp.generated.resources.ic_filter_solaris
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
import org.app.carlos.viewModel.SettingsViewModel
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavHostController,
    viewModel: HistoryViewModel = koinInject(),
    homeViewModel: HomeViewModel = koinInject(),
    settingsViewModel: SettingsViewModel
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

    val settingsState by settingsViewModel.uiState.collectAsState()
    val selectedTheme = settingsState.themes.firstOrNull { it.isSelected }

    val fileExporter = provideFileExporter()

    val text = when (selectedTheme?.id) {
        "default" -> Color.White
        "midnight" -> Color.White
        "solaris" -> Color.Black
        "marine" -> Color.White
        else -> Color.White
    }

    val backgroundRes = when (selectedTheme?.id) {
        "default" -> Res.drawable.bg_default
        "midnight" -> Res.drawable.bg_midnight
        "solaris" -> Res.drawable.bg_solaris
        "marine" -> Res.drawable.bg_marine
        else -> Res.drawable.bg_default
    }
    val overlayAlpha = if (selectedTheme?.id == "solaris") 0f else 0.4f

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
                        title = { Text("History", fontWeight = FontWeight.Bold, color = text) },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),

                    actions = {
                        IconButton(onClick = { showFilters = !showFilters }) {
                            val filterImageRes = if (selectedTheme?.id == "solaris") {
                                Res.drawable.ic_filter_solaris
                            } else {
                                Res.drawable.ic_filter_default
                            }

                            Image(
                                painter = painterResource(filterImageRes),
                                contentDescription = "Filters",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        TextButton(onClick = { viewModel.exportHistory(fileExporter) }) {
                            Text("Export", color = text)
                        }
                    }
                )
            },
            bottomBar = {
                BottomNavBar(
                    navController = navController,
                    currentRoute = currentRoute,
                    selectedThemeId = selectedTheme?.id
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {

                if (showFilters) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent)
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
                                placeholder = {
                                    Text(
                                        "Search by title or notes",
                                        color = text.copy(alpha = 0.5f)
                                    )
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = null, tint = text)
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = text)
                                        }
                                    }
                                },
                                textStyle = LocalTextStyle.current.copy(color = text),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = when (selectedTheme?.id) {
                                            "default" -> Color(0xFF001BA6)
                                            "midnight" -> Color(0xFF1D1B49)
                                            "solaris" -> Color.White
                                            "marine" -> Color(0xFF22272E)
                                            else -> Color(0xFF001BA6)
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (selectedTheme?.id == "solaris") Color(0xFFFFF315) else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    disabledBorderColor = Color.Transparent,
                                    errorBorderColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    errorContainerColor = Color.Transparent
                                ),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )

                            Text("Filters", fontWeight = FontWeight.SemiBold, color = text)
                            val chipColor = when (selectedTheme?.id) {
                                "default" -> Color(0xFF0BFFFF)
                                "midnight" -> Color(0xFFB421FF)
                                "solaris" -> Color(0xFFFFC654)
                                "marine" -> Color(0xFF37FFE6)
                                else -> Color(0xFF0BFFFF)
                            }

                            val chipBorderColor = chipColor
                            val chipSelectedColor = when (selectedTheme?.id) {
                                "default" -> Color(0xFF0BFFFF)
                                "midnight" -> Color(0xFFB421FF)
                                "solaris" -> Color(0xFFFFC654)
                                "marine" -> Color(0xFF37FFE6)
                                else -> Color(0xFF0BFFFF)
                            }

                            val chipTextColor = chipColor
                            val chipSelectedTextColor = if (selectedTheme?.id == "solaris") Color.Black else Color.White

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
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
                                        label = {
                                            Text(
                                                category,
                                                color = if (selectedCategories.contains(category)) chipSelectedTextColor else chipTextColor,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        },
                                        modifier = Modifier
                                            .widthIn(min = 65.dp, max = 110.dp)
                                            .height(40.dp),
                                        border = BorderStroke(
                                            1.dp,
                                            if (selectedCategories.contains(category)) chipSelectedColor else chipBorderColor
                                        ),
                                        colors = FilterChipDefaults.filterChipColors(
                                            containerColor = Color.Transparent,
                                            selectedContainerColor = Color.Transparent
                                        )
                                    )
                                }
                            }

                            Text("Date range", fontWeight = FontWeight.SemiBold, color = text)
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                                Box {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                when (selectedTheme?.id) {
                                                    "default" -> Color(0xFF001BA6)
                                                    "midnight" -> Color(0xFF1D1B49)
                                                    "solaris" -> Color.White
                                                    "marine" -> Color(0xFF22272E)
                                                    else -> Color(0xFF001BA6)
                                                }
                                            )
                                            .clickable { showFromCalendar = true },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .width(80.dp)
                                                .background(
                                                    if (selectedTheme?.id == "solaris") Color(0xFFF0F0F0)
                                                    else (when (selectedTheme?.id) {
                                                        "default" -> Color(0xFF001BA6)
                                                        "midnight" -> Color(0xFF1D1B49)
                                                        "marine" -> Color(0xFF22272E)
                                                        else -> Color(0xFF001BA6)
                                                    }).copy(alpha = 0.8f)
                                                )
                                                .padding(start = 12.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            Text(
                                                "From:",
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 16.sp,
                                                color = if (selectedTheme?.id == "solaris") Color.Black else Color.White
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .weight(1f)
                                                .padding(horizontal = 12.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = dateFrom?.let {
                                                        "${it.dayOfMonth.toString().padStart(2, '0')}/${it.monthNumber.toString().padStart(2, '0')}/${it.year}"
                                                    } ?: "Select date",
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 16.sp,
                                                    color = if (selectedTheme?.id == "solaris") Color.Black else Color.White,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Icon(
                                                    Icons.Default.CalendarToday,
                                                    contentDescription = "Pick date",
                                                    tint = if (selectedTheme?.id == "solaris") Color.Black else Color.White
                                                )
                                            }
                                        }
                                    }

                                    if (showFromCalendar) {
                                        Popup(
                                            onDismissRequest = { showFromCalendar = false }
                                        ) {
                                            CalendarView(
                                                selectedDate = dateFrom,
                                                onDateSelected = {
                                                    dateFrom = it
                                                    showFromCalendar = false
                                                },
                                                selectedThemeId = selectedTheme?.id
                                            )
                                        }
                                    }
                                }

                                Box {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                when (selectedTheme?.id) {
                                                    "default" -> Color(0xFF001BA6)
                                                    "midnight" -> Color(0xFF1D1B49)
                                                    "solaris" -> Color.White
                                                    "marine" -> Color(0xFF22272E)
                                                    else -> Color(0xFF001BA6)
                                                }
                                            )
                                            .clickable { showToCalendar = true },
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .width(80.dp)
                                                .background(
                                                    if (selectedTheme?.id == "solaris") Color(0xFFF0F0F0)
                                                    else (when (selectedTheme?.id) {
                                                        "default" -> Color(0xFF001BA6)
                                                        "midnight" -> Color(0xFF1D1B49)
                                                        "marine" -> Color(0xFF22272E)
                                                        else -> Color(0xFF001BA6)
                                                    }).copy(alpha = 0.8f)
                                                )
                                                .padding(start = 12.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            Text(
                                                "To:",
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 16.sp,
                                                color = if (selectedTheme?.id == "solaris") Color.Black else Color.White
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .weight(1f)
                                                .padding(horizontal = 12.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = dateTo?.let {
                                                        "${it.dayOfMonth.toString().padStart(2, '0')}/${it.monthNumber.toString().padStart(2, '0')}/${it.year}"
                                                    } ?: "Select date",
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 16.sp,
                                                    color = if (selectedTheme?.id == "solaris") Color.Black else Color.White,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Icon(
                                                    Icons.Default.CalendarToday,
                                                    contentDescription = "Pick date",
                                                    tint = if (selectedTheme?.id == "solaris") Color.Black else Color.White
                                                )
                                            }
                                        }
                                    }

                                    if (showToCalendar) {
                                        Popup(
                                            onDismissRequest = { showToCalendar = false }
                                        ) {
                                            CalendarView(
                                                selectedDate = dateTo,
                                                onDateSelected = {
                                                    dateTo = it
                                                    showToCalendar = false
                                                },
                                                selectedThemeId = selectedTheme?.id
                                            )
                                        }
                                    }
                                }
                            }

                            Text("Amount", fontWeight = FontWeight.SemiBold, color = text)
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            when (selectedTheme?.id) {
                                                "default" -> Color(0xFF001BA6)
                                                "midnight" -> Color(0xFF1D1B49)
                                                "solaris" -> Color.White
                                                "marine" -> Color(0xFF22272E)
                                                else -> Color(0xFF001BA6)
                                            }
                                        ),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {

                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .width(60.dp)
                                                .background(
                                                    if (selectedTheme?.id == "solaris") Color(0xFFF0F0F0)
                                                    else (when (selectedTheme?.id) {
                                                        "default" -> Color(0xFF001BA6)
                                                        "midnight" -> Color(0xFF1D1B49)
                                                        "marine" -> Color(0xFF22272E)
                                                        else -> Color(0xFF001BA6)
                                                    }).copy(alpha = 0.8f)
                                                )
                                                .padding(start = 12.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            Text(
                                                "Min:",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = if (selectedTheme?.id == "solaris") Color.Black else Color.White
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .weight(1f)
                                                .padding(horizontal = 12.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            BasicTextField(
                                                value = amountMin,
                                                onValueChange = { amountMin = it.filter { c -> c.isDigit() || c == '.' } },
                                                singleLine = true,
                                                textStyle = LocalTextStyle.current.copy(
                                                    color = if (amountMin.isEmpty()) Color.Gray else text,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                ),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                if (amountMin.isEmpty()) {
                                                    Text(
                                                        "$0.00",
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = Color.Gray
                                                    )
                                                }
                                                it()
                                            }
                                        }
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            when (selectedTheme?.id) {
                                                "default" -> Color(0xFF001BA6)
                                                "midnight" -> Color(0xFF1D1B49)
                                                "solaris" -> Color.White
                                                "marine" -> Color(0xFF22272E)
                                                else -> Color(0xFF001BA6)
                                            }
                                        ),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {

                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .width(60.dp)
                                                .background(
                                                    if (selectedTheme?.id == "solaris") Color(0xFFF0F0F0)
                                                    else (when (selectedTheme?.id) {
                                                        "default" -> Color(0xFF001BA6)
                                                        "midnight" -> Color(0xFF1D1B49)
                                                        "marine" -> Color(0xFF22272E)
                                                        else -> Color(0xFF001BA6)
                                                    }).copy(alpha = 0.8f)
                                                )
                                                .padding(start = 12.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            Text(
                                                "Max:",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = if (selectedTheme?.id == "solaris") Color.Black else Color.White
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .weight(1f)
                                                .padding(horizontal = 12.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            BasicTextField(
                                                value = amountMax,
                                                onValueChange = { amountMax = it.filter { c -> c.isDigit() || c == '.' } },
                                                singleLine = true,
                                                textStyle = LocalTextStyle.current.copy(
                                                    color = if (amountMax.isEmpty()) Color.Gray else text,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                ),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                if (amountMax.isEmpty()) {
                                                    Text(
                                                        "$0.00",
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = Color.Gray
                                                    )
                                                }
                                                it()
                                            }
                                        }
                                    }
                                }
                            }

                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
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
                                        viewModel.applyFilters()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Reset filters", color = Color.Black, fontWeight = FontWeight.Bold)
                                }

                                val buttonColor = when (selectedTheme?.id) {
                                    "default" -> Color(0xFFFFF315)
                                    "midnight" -> Color(0xFFB421FF)
                                    "solaris" -> Color(0xFFFFC654)
                                    "marine" -> Color(0xFF37FFE6)
                                    else -> Color(0xFFFFF315)
                                }
                                val buttonTextColor = if (selectedTheme?.id == "solaris") Color.Black else Color.White

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
                                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Apply", color = buttonTextColor, fontWeight = FontWeight.Bold)
                                }
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
                            val emptyImageRes = when (selectedTheme?.id) {
                                "default" -> Res.drawable.empty_default_history
                                "midnight" -> Res.drawable.empty_midnight_history
                                "solaris" -> Res.drawable.empty_solaris_history
                                "marine" -> Res.drawable.empty_marine_history
                                else -> Res.drawable.empty_default_history
                            }

                            Image(
                                painter = painterResource(emptyImageRes),
                                contentDescription = "Empty state",
                                modifier = Modifier.size(200.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        uiState.groups.forEach { (ym, expenses) ->
                            stickyHeader {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    val total = uiState.totals[ym] ?: 0.0
                                    Text(
                                        "${ym} — ${formatAmount(total)}",
                                        fontWeight = FontWeight.Bold,
                                        color = text
                                    )
                                }
                            }
                            items(expenses, key = { it.id!! }) { exp ->
                                SwipeToDismissItem(
                                    expense = exp,
                                    navController = navController,
                                    selectedTheme = selectedTheme,
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
}