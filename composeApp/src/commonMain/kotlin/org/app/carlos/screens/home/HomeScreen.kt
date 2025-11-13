package org.app.carlos.screens.home

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
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import carlosapp.composeapp.generated.resources.Res
import carlosapp.composeapp.generated.resources.bg_default
import carlosapp.composeapp.generated.resources.bg_marine
import carlosapp.composeapp.generated.resources.bg_midnight
import carlosapp.composeapp.generated.resources.bg_solaris
import carlosapp.composeapp.generated.resources.ic_add_default
import carlosapp.composeapp.generated.resources.ic_add_marine
import carlosapp.composeapp.generated.resources.ic_add_midnight
import carlosapp.composeapp.generated.resources.ic_add_solaris
import carlosapp.composeapp.generated.resources.ic_empty_expenses
import carlosapp.composeapp.generated.resources.ic_settings_default
import carlosapp.composeapp.generated.resources.ic_settings_marine
import carlosapp.composeapp.generated.resources.ic_settings_midnight
import carlosapp.composeapp.generated.resources.ic_settings_solaris
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.app.carlos.screens.Screen
import org.app.carlos.screens.bottom.BottomNavBar
import org.app.carlos.screens.list.formatAmount
import org.app.carlos.screens.search.SearchScreen
import org.app.carlos.viewModel.HomeViewModel
import org.app.carlos.viewModel.PeriodType
import org.app.carlos.viewModel.SettingsViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import kotlin.math.round

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = koinInject(),
    settingsViewModel: SettingsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route
    val settingsState by settingsViewModel.uiState.collectAsState()
    val selectedTheme = settingsState.themes.firstOrNull { it.isSelected }

    val text = when (selectedTheme?.id) {
        "default" -> Color.White
        "midnight" -> Color.White
        "solaris" -> Color.Black
        "marine" -> Color.White
        else -> Color.White
    }

    val background = when (selectedTheme?.id) {
        "default" -> Color(0xFF08155C)
        "midnight" -> Color(0xFF0E0C33)
        "solaris" -> Color(0xFFFFDE80)
        "marine" -> Color(0xFF0A0A0A)
        else -> Color(0xFF08155C)
    }

    val cardBackgroundColor = when (selectedTheme?.id) {
        "default" -> Color(0xFF4F6BFF)
        "midnight" -> Color(0xFF2A2767)
        "solaris" -> Color(0xFFFFFDF4)
        "marine" -> Color(0xFF22272E)
        else -> Color(0xFF4F6BFF)
    }

    val periodRowColor = when (selectedTheme?.id) {
        "default" -> Color(0xFF4F6BFF)
        "midnight" -> Color(0xFF2A2767)
        "solaris" -> Color(0xFFFFFDF4)
        "marine" -> Color(0xFF22272E)
        else -> Color(0xFF4F6BFF)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (selectedTheme?.id == "solaris") {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            )
        } else {
            val backgroundRes = when (selectedTheme?.id) {
                "default" -> Res.drawable.bg_default
                "midnight" -> Res.drawable.bg_midnight
                "marine" -> Res.drawable.bg_marine
                else -> Res.drawable.bg_default
            }
            Image(
                painter = painterResource(backgroundRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        val overlayAlpha = if (selectedTheme?.id == "solaris") 0f else 0.4f
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = overlayAlpha))
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                            val settingsRes = when (selectedTheme?.id) {
                                "default" -> Res.drawable.ic_settings_default
                                "midnight" -> Res.drawable.ic_settings_midnight
                                "solaris" -> Res.drawable.ic_settings_solaris
                                "marine" -> Res.drawable.ic_settings_marine
                                else -> Res.drawable.ic_settings_default
                            }

                            Image(
                                painter = painterResource(settingsRes),
                                contentDescription = "Settings",
                                modifier = Modifier.size(24.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    },
                    title = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Dashboard", color = text)
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate("${Screen.AddEditExpense.route}?expenseId=-1") }) {
                            val addRes = when (selectedTheme?.id) {
                                "default" -> Res.drawable.ic_add_default
                                "midnight" -> Res.drawable.ic_add_midnight
                                "solaris" -> Res.drawable.ic_add_solaris
                                "marine" -> Res.drawable.ic_add_marine
                                else -> Res.drawable.ic_add_default
                            }

                            Image(
                                painter = painterResource(addRes),
                                contentDescription = "Add Expense",
                                modifier = Modifier.size(45.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = text
                    )
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
        ) { paddingValues ->
            if (uiState.isEmpty) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    Image(
                        painter = painterResource(Res.drawable.ic_empty_expenses),
                        contentDescription = "No expenses",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(200.dp),
                        contentScale = ContentScale.Fit
                    )

                    val buttonColor = when (selectedTheme?.id) {
                        "default" -> Color(0xFFFDDB2C)
                        "midnight" -> Color(0xFFB421FF)
                        "solaris" -> Color(0xFFFFC654)
                        "marine" -> Color(0xFF37FFE6)
                        else -> Color(0xFFFDDB2C)
                    }

                    Button(
                        onClick = { navController.navigate("${Screen.AddEditExpense.route}?expenseId=-1") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 24.dp)
                            .align(Alignment.BottomCenter)
                            .height(52.dp)
                            .shadow(
                                elevation = 12.dp,
                                shape = RoundedCornerShape(12.dp),
                                clip = false
                            ),
                        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text(
                            text = "Add your first expense",
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "Period Summary",
                            color = text,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier
                                .padding(
                                    start = 2.dp,
                                    bottom = 2.dp
                                )
                        )
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .then(
                                    if (selectedTheme?.id == "solaris") {
                                        Modifier.border(
                                            width = 1.dp,
                                            color = Color(0xFFFFC654),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    } else Modifier
                                ),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBackgroundColor)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "This Month",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = text
                                )
                                Text(
                                    "$${uiState.totalThisMonth}",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = text
                                )
                                val diff = uiState.totalThisMonth - uiState.totalLastMonth
                                val trendSymbol = when {
                                    diff > 0 -> "↑"
                                    diff < 0 -> "↓"
                                    else -> "→"
                                }
                                Text(
                                    buildAnnotatedString {
                                        append("Last month: $${uiState.totalLastMonth} ")
                                        withStyle(
                                            SpanStyle(
                                                color = if (diff > 0) Color.Green else if (diff < 0) Color.Red else Color.Gray
                                            )
                                        ) {
                                            append(trendSymbol)
                                        }
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = text
                                )

                                Spacer(Modifier.height(16.dp))

                                val selectedPeriod =
                                    uiState.period

                                val selectedColor = when (selectedTheme?.id) {
                                    "default" -> Color(0xFFFFC82C)
                                    "midnight" -> Color(0xFFB421FF)
                                    "solaris" -> Color(0xFF130D00)
                                    "marine" -> Color(0xFF37FFE6)
                                    else -> Color(0xFFFFC82C)
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(36.dp)
                                        .background(
                                            color = background,
                                            shape = RoundedCornerShape(8.dp)
                                        ),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    PeriodType.values().forEachIndexed { index, period ->
                                        val isSelected = period == selectedPeriod

                                        val horizontalPadding = if (isSelected) {
                                            when (index) {
                                                1 -> 2.dp
                                                else -> 0.dp
                                            }
                                        } else 4.dp

                                        val verticalPadding = if (isSelected) 0.dp else 4.dp

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight()
                                                .padding(vertical = verticalPadding, horizontal = horizontalPadding)
                                                .background(
                                                    color = if (isSelected) selectedColor else Color.Transparent,
                                                    shape = if (isSelected) RoundedCornerShape(8.dp) else RoundedCornerShape(0.dp)
                                                )
                                                .clickable { viewModel.changePeriod(period) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            val textColor = if (isSelected) {
                                                if (selectedTheme?.id == "marine" || selectedTheme?.id == "default") Color.Black else Color.White
                                            } else {
                                                text
                                            }

                                            Text(
                                                text = when (period) {
                                                    PeriodType.MONTH -> "Month"
                                                    PeriodType.YEAR -> "Year"
                                                    PeriodType.ALL -> "All"
                                                },
                                                color = textColor,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Column(modifier = Modifier.padding(horizontal = 2.dp, vertical = 2.dp)) {
                            Text(
                                text = "Quick Actions",
                                style = MaterialTheme.typography.titleMedium,
                                color = text
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            val buttonColors = when (selectedTheme?.id) {
                                "default" -> ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF00FCFF),
                                    contentColor = Color(0xFF1F2B9D)
                                )
                                "midnight" -> ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1D1B49),
                                    contentColor = Color.White
                                )
                                "solaris" -> ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Color.Black
                                )
                                "marine" -> ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF22272E),
                                    contentColor = Color.White
                                )
                                else -> ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF00FCFF),
                                    contentColor = Color(0xFF1F2B9D)
                                )
                            }

                            fun buttonModifier(): Modifier {
                                val base = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minWidth = 100.dp)

                                return if (selectedTheme?.id == "solaris") {
                                    base.then(
                                        Modifier.border(
                                            width = 1.dp,
                                            color = Color(0xFFFFC654),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    )
                                } else base
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { navController.navigate("${Screen.AddEditExpense.route}?expenseId=-1") },
                                    colors = buttonColors,
                                    shape = RoundedCornerShape(8.dp),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                                    modifier = buttonModifier()
                                ) {
                                    Text(
                                        "+Expense",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Clip,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                Button(
                                    onClick = { navController.navigate(Screen.Statistics.route) },
                                    colors = buttonColors,
                                    shape = RoundedCornerShape(8.dp),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                                    modifier = buttonModifier()
                                ) {
                                    Text(
                                        "Statistics",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                Button(
                                    onClick = { navController.navigate(Screen.History.route) },
                                    colors = buttonColors,
                                    shape = RoundedCornerShape(8.dp),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                                    modifier = buttonModifier()
                                ) {
                                    Text(
                                        "History",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "By Category",
                                style = MaterialTheme.typography.titleMedium,
                                color = text
                            )
                            Spacer(Modifier.height(12.dp))

                            val categories = listOf("Fuel", "Insurance", "Repair", "Other")
                            val rows = categories.chunked(2)

                            val rowBackground = when (selectedTheme?.id) {
                                "default" -> Color(0xFF4F6BFF)
                                "midnight" -> Color(0xFF1D1B49)
                                "solaris" -> Color(0xFFFFDE80)
                                "marine" -> Color(0xFF22272E)
                                else -> Color(0xFF4F6BFF)
                            }

                            val iconBackground = when (selectedTheme?.id) {
                                "default" -> Color(0xFF394FD2)
                                "midnight" -> Color(0xFF0E0C33)
                                "solaris" -> Color.Black
                                "marine" -> Color(0xFF0A0A0A)
                                else -> Color(0xFF394FD2)
                            }

                            rows.forEachIndexed { index, rowItems ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    rowItems.forEach { category ->
                                        Row(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(
                                                    color = rowBackground,
                                                    RoundedCornerShape(6.dp)
                                                )
                                                .padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .background(
                                                        color = iconBackground,
                                                        RoundedCornerShape(4.dp)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                val icon = when (category) {
                                                    "Fuel" -> Icons.Default.LocalGasStation
                                                    "Insurance" -> Icons.Default.Security
                                                    "Repair" -> Icons.Default.Build
                                                    else -> Icons.Default.MoreHoriz
                                                }
                                                Icon(
                                                    icon,
                                                    contentDescription = null,
                                                    tint = Color.White
                                                )
                                            }

                                            Spacer(Modifier.width(8.dp))

                                            Column {
                                                Text(
                                                    category,
                                                    color = text,
                                                    fontSize = 13.sp
                                                )
                                                Text(
                                                    "$${(uiState.byCategory[category] ?: 0.0).toTwoDecimals()}",
                                                    color = text,
                                                    fontSize = 13.sp
                                                )
                                            }
                                        }
                                    }
                                }

                                if (index != rows.lastIndex) {
                                    Spacer(Modifier.height(12.dp))
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Recent", style = MaterialTheme.typography.titleMedium, color = text)
                            val seeAllColor = when (selectedTheme?.id) {
                                "default" -> Color(0xFFFDDB2C)
                                "midnight" -> Color(0xFFB421FF)
                                "solaris" -> Color.Gray
                                "marine" -> Color(0xFF37FFE6)
                                else -> Color(0xFFFDDB2C)
                            }

                            TextButton(onClick = { navController.navigate(Screen.History.route) }) {
                                Text(
                                    "See all",
                                    color = seeAllColor
                                )
                            }
                        }
                    }

                    items(uiState.recent) { expense ->
                        val rowBackground = when (selectedTheme?.id) {
                            "default" -> Color(0xFF4F6BFF)
                            "midnight" -> Color(0xFF1D1B49)
                            "solaris" -> Color(0xFFFFDE80)
                            "marine" -> Color(0xFF22272E)
                            else -> Color(0xFF4F6BFF)
                        }

                        val iconBackground = when (selectedTheme?.id) {
                            "default" -> Color(0xFF394FD2)
                            "midnight" -> Color(0xFF0E0C33)
                            "solaris" -> Color.Black
                            "marine" -> Color(0xFF0A0A0A)
                            else -> Color(0xFF394FD2)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(rowBackground, RoundedCornerShape(4.dp))
                                .clickable {
                                    navController.navigate(
                                        Screen.Details.createRoute(expense.id)
                                    )
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val icon = when (expense.category) {
                                    "Fuel" -> Icons.Default.LocalGasStation
                                    "Insurance" -> Icons.Default.Security
                                    "Repair" -> Icons.Default.Build
                                    else -> Icons.Default.MoreHoriz
                                }
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(iconBackground, RoundedCornerShape(4.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(icon, contentDescription = null, tint = Color.White)
                                }

                                Spacer(Modifier.width(4.dp))

                                Column {
                                    Text(
                                        expense.title?.takeIf { it.isNotBlank() } ?: expense.category,
                                        fontWeight = FontWeight.Bold,
                                        color = text
                                    )
                                    Text(
                                        expense.date,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = text
                                    )
                                }
                            }
                            Text(
                                formatAmount(expense.amount),
                                fontWeight = FontWeight.Bold,
                                color = text
                            )
                        }
                    }

                    item {
                        Text(
                            "Planned",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(start = 2.dp, bottom = 2.dp),
                            color = text
                        )
                    }

                    items(uiState.planned) { expense ->
                        val rowBackground = when (selectedTheme?.id) {
                            "default" -> Color(0xFF4F6BFF)
                            "midnight" -> Color(0xFF1D1B49)
                            "solaris" -> Color(0xFFFFDE80)
                            "marine" -> Color(0xFF22272E)
                            else -> Color(0xFF4F6BFF)
                        }

                        val iconBackground = when (selectedTheme?.id) {
                            "default" -> Color(0xFF394FD2)
                            "midnight" -> Color(0xFF0E0C33)
                            "solaris" -> Color.Black
                            "marine" -> Color(0xFF0A0A0A)
                            else -> Color(0xFF394FD2)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .background(rowBackground, RoundedCornerShape(8.dp))
                                .clickable {
                                    navController.navigate(
                                        Screen.Details.createRoute(expense.id!!)
                                    )
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(iconBackground, RoundedCornerShape(4.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Event, contentDescription = null, tint = Color.White)
                                }

                                Spacer(Modifier.width(8.dp))

                                Column {
                                    val today = Clock.System.now()
                                        .toLocalDateTime(TimeZone.currentSystemDefault()).date
                                    val expenseDate = LocalDate.parse(expense.date)
                                    val daysLeft = expenseDate.toEpochDays() - today.toEpochDays()
                                    Text(
                                        "${expense.title ?: expense.category} (in $daysLeft days)",
                                        fontWeight = FontWeight.Bold,
                                        color = text
                                    )
                                    Text(
                                        expense.date,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = text
                                    )
                                }
                            }

                            Text(
                                formatAmount(expense.amount),
                                fontWeight = FontWeight.Bold,
                                color = text
                            )
                        }
                    }
                }
            }
        }
    }
}


fun Double.toTwoDecimals(): String {
    return (round(this * 100) / 100).toString()
}