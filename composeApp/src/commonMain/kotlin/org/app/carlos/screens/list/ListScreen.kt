package org.app.carlos.screens.list

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import carlosapp.composeapp.generated.resources.Res
import carlosapp.composeapp.generated.resources.bg_default
import carlosapp.composeapp.generated.resources.bg_marine
import carlosapp.composeapp.generated.resources.bg_midnight
import carlosapp.composeapp.generated.resources.bg_solaris
import carlosapp.composeapp.generated.resources.ic_filter_default
import carlosapp.composeapp.generated.resources.ic_filter_solaris
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.app.carlos.screens.calenda.CalendarView
import org.app.carlos.viewModel.HomeViewModel
import org.app.carlos.viewModel.SettingsViewModel
import org.app.carlos.viewModel.SortOrder
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    navController: NavHostController,
    searchViewModel: SearchViewModel = koinInject(),
    homeViewModel: HomeViewModel = koinInject(),
    settingsViewModel: SettingsViewModel
) {
    val uiState by searchViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    var showSortMenu by remember { mutableStateOf(false) }
    var showFilters by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategories by remember { mutableStateOf(setOf<String>()) }
    var dateFrom by remember { mutableStateOf<LocalDate?>(null) }
    var dateTo by remember { mutableStateOf<LocalDate?>(null) }
    var amountMin by remember { mutableStateOf("") }
    var amountMax by remember { mutableStateOf("") }

    var showFromCalendar by remember { mutableStateOf(false) }
    var showToCalendar by remember { mutableStateOf(false) }

    val settingsState by settingsViewModel.uiState.collectAsState()
    val selectedTheme = settingsState.themes.firstOrNull { it.isSelected }

    val backgroundRes = when (selectedTheme?.id) {
        "default" -> Res.drawable.bg_default
        "midnight" -> Res.drawable.bg_midnight
        "solaris" -> Res.drawable.bg_solaris
        "marine" -> Res.drawable.bg_marine
        else -> Res.drawable.bg_default
    }

    val textColor = if (selectedTheme?.id == "solaris") Color.Black else Color.White
    val overlayAlpha = if (selectedTheme?.id == "solaris") 0f else 0.4f

    val categories = listOf("Fuel", "Insurance", "Repair", "Other")

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
                            uiState.title,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = textColor
                            )
                        }
                    },
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
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = showSortMenu,
                    onExpandedChange = { showSortMenu = !showSortMenu },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    val containerColor = when (selectedTheme?.id) {
                        "default" -> Color(0xFF001BA6)
                        "midnight" -> Color(0xFF1D1B49)
                        "solaris" -> Color(0xFFFFF5D7)
                        "marine" -> Color(0xFF22272E)
                        else -> Color(0xFF001BA6)
                    }

                    val textColor = when (selectedTheme?.id) {
                        "default" -> Color.White
                        "midnight" -> Color.White
                        "solaris" -> Color.Black
                        "marine" -> Color.White
                        else -> Color.White
                    }

                    OutlinedTextField(
                        value = uiState.sortOrder.label,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            CompositionLocalProvider(LocalContentColor provides textColor) {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = showSortMenu)
                            }
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = containerColor,
                            unfocusedContainerColor = containerColor,
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false },
                        modifier = Modifier.background(containerColor)
                    ) {
                        SortOrder.values().forEach { order ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        order.label,
                                        color = if (selectedTheme?.id == "solaris") Color.Black else Color.White
                                    )
                                },
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
                                        color = textColor.copy(alpha = 0.5f)
                                    )
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = null, tint = textColor)
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = textColor)
                                        }
                                    }
                                },
                                textStyle = LocalTextStyle.current.copy(color = textColor),
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

                            Text("Filters", fontWeight = FontWeight.SemiBold, color = textColor)
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
                                                text = category,
                                                color = if (selectedCategories.contains(category))
                                                    chipSelectedTextColor else chipTextColor,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 1,
                                                softWrap = false,
                                                overflow = TextOverflow.Clip
                                            )
                                        },
                                        modifier = Modifier
                                            .height(40.dp)
                                            .padding(horizontal = 2.dp),
                                        border = BorderStroke(
                                            1.dp,
                                            if (selectedCategories.contains(category))
                                                chipSelectedColor else chipBorderColor
                                        ),
                                        colors = FilterChipDefaults.filterChipColors(
                                            containerColor = Color.Transparent,
                                            selectedContainerColor = Color.Transparent
                                        )
                                    )
                                }
                            }

                            Text("Date range", fontWeight = FontWeight.SemiBold, color = textColor)
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

                            Text("Amount", fontWeight = FontWeight.SemiBold, color = textColor)
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
                                                    color = if (amountMin.isEmpty()) Color.Gray else textColor,
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
                                                    color = if (amountMax.isEmpty()) Color.Gray else textColor,
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

                            val buttonColor = when (selectedTheme?.id) {
                                "default" -> Color(0xFFFFF315)
                                "midnight" -> Color(0xFFB421FF)
                                "solaris" -> Color(0xFFFFC654)
                                "marine" -> Color(0xFF37FFE6)
                                else -> Color(0xFFFFF315)
                            }

                            val buttonTextColor = when (selectedTheme?.id) {
                                "default", "solaris", "marine" -> Color.Black
                                "midnight" -> Color.White
                                else -> Color.Black
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
                                        searchViewModel.search(null, emptySet(), null, null, "", "")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Reset filters", color = Color.Black, fontWeight = FontWeight.Bold)
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
                                        navController.navigate(Screen.List.route)
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
                    EmptyListState(
                        selectedThemeId = selectedTheme?.id,
                        onClearFilters = {
                            searchQuery = ""
                            selectedCategories = emptySet()
                            dateFrom = null
                            dateTo = null
                            amountMin = ""
                            amountMax = ""
                            searchViewModel.search(null, emptySet(), null, null, "", "")
                        }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().heightIn(max = 600.dp)
                    ) {
                        items(uiState.results, key = { it.id!! }) { expense ->
                            SwipeToDismissItem(
                                expense = expense,
                                navController = navController,
                                selectedTheme = selectedTheme,
                                onDelete = { searchViewModel.confirmDelete(expense) }
                            )
                        }
                    }
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

        if (uiState.showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { searchViewModel.dismissDeleteDialog() },
                title = {
                    Text(
                        "Delete expense?",
                        color = textColor
                    )
                },
                text = {
                    Text(
                        "This action cannot be undone.",
                        color = textColor
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
                            onClick = { searchViewModel.dismissDeleteDialog() },
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
                                searchViewModel.deleteConfirmed()
                                homeViewModel.refreshData()
                                searchViewModel.search()
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
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}