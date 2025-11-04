package org.app.carlos.screens.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.app.carlos.data.model.Theme
import org.app.carlos.viewModel.PeriodType
import org.app.carlos.viewModel.ThemeUiState
import org.app.carlos.viewModel.YearMonth

@Composable
fun PeriodSelector(
    selectedPeriod: PeriodType,
    selectedMonth: YearMonth?,
    selectedYear: Int?,
    onPeriodChange: (PeriodType) -> Unit,
    onMonthChange: (YearMonth) -> Unit,
    onYearChange: (Int) -> Unit,
    selectedTheme: ThemeUiState?
) {
    val saveButtonColor = when (selectedTheme?.id) {
        "default" -> Color.White
        "midnight" -> Color(0xFFB421FF)
        "solaris" -> Color(0xFFFFC654)
        "marine" -> Color(0xFF37FFE6)
        else -> Color(0xFFFDDB2C)
    }

    val textColor = when (selectedTheme?.id) {
        "default", "midnight", "marine" -> Color.White
        "solaris" -> Color.Black
        else -> Color.White
    }

    Column {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            PeriodType.entries.forEach { type ->
                FilterChip(
                    selected = type == selectedPeriod,
                    onClick = { onPeriodChange(type) },
                    label = {
                        Text(
                            type.name,
                            color = if (type == selectedPeriod) {
                                if (selectedTheme?.id in listOf("solaris", "marine", "default")) Color.Black else Color.White
                            } else textColor
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = saveButtonColor,
                        containerColor = Color.Transparent
                    ),
                    border = null,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .height(36.dp)
                        .padding(horizontal = 16.dp)
                )
            }
        }

        when (selectedPeriod) {
            PeriodType.MONTH -> {
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                val currentYear = selectedYear ?: now.year
                val months = (1..12).map { YearMonth(currentYear, it) }

                DropdownSelector(
                    items = months,
                    selectedItem = selectedMonth ?: YearMonth(currentYear, now.monthNumber),
                    label = { ym -> monthName(ym.month) },
                    onItemSelected = { onMonthChange(it) },
                    selectedTheme = selectedTheme
                )
            }
            PeriodType.YEAR -> {
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                val years = (now.year - 5..now.year).toList()

                DropdownSelector(
                    items = years,
                    selectedItem = selectedYear ?: now.year,
                    label = { it.toString() },
                    onItemSelected = { onYearChange(it) },
                    selectedTheme = selectedTheme
                )
            }
            PeriodType.ALL -> {}
        }
    }
}

fun monthName(month: Int): String {
    return when (month) {
        1 -> "January"
        2 -> "February"
        3 -> "March"
        4 -> "April"
        5 -> "May"
        6 -> "June"
        7 -> "July"
        8 -> "August"
        9 -> "September"
        10 -> "October"
        11 -> "November"
        12 -> "December"
        else -> "Unknown"
    }
}

@Composable
fun <T> DropdownSelector(
    items: List<T>,
    selectedItem: T?,
    label: (T) -> String,
    onItemSelected: (T) -> Unit,
    selectedTheme: ThemeUiState?
) {
    var expanded by remember { mutableStateOf(false) }

    val buttonTextColor = when (selectedTheme?.id) {
        "default" -> Color.White
        "midnight" -> Color(0xFFBDBDBD)
        "solaris" -> Color.Black
        "marine" -> Color.White
        else -> Color.White
    }

    val triangleColor = when (selectedTheme?.id) {
        "default" -> Color(0xFFFFE100)
        "midnight" -> Color(0xFFB421FF)
        "solaris" -> Color(0xFFFFCC4A)
        "marine" -> Color(0xFF37FFE6)
        else -> Color.White
    }

    val menuBackgroundColor = when (selectedTheme?.id) {
        "default" -> Color(0xFF1B2D8A)
        "midnight" -> Color(0xFF1D1B49)
        "solaris" -> Color(0xFFFFE8A5)
        "marine" -> Color(0xFF22272E)
        else -> Color(0xFF1B2D8A)
    }

    val selectedItemColor = when (selectedTheme?.id) {
        "default" -> Color(0xFFFFF315)
        "midnight" -> Color(0xFFB421FF)
        "solaris" -> Color(0xFFFFC654)
        "marine" -> Color(0xFF37FFE6)
        else -> Color(0xFFFFF315)
    }

    val itemTextColor = when (selectedTheme?.id) {
        "default", "midnight", "marine" -> Color.White
        "solaris" -> Color.Black
        else -> Color.White
    }

    Box {
        TextButton(
            onClick = { expanded = true },
            colors = ButtonDefaults.textButtonColors(contentColor = buttonTextColor)
        ) {
            Text(selectedItem?.let(label) ?: "Select", color = buttonTextColor)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = triangleColor)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(menuBackgroundColor)
        ) {
            items.forEach { item ->
                val isSelected = item == selectedItem
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onItemSelected(item)
                    },
                    text = {
                        Text(
                            label(item),
                            color = if (isSelected) selectedItemColor else itemTextColor
                        )
                    },
                    modifier = Modifier.background(
                        if (isSelected) menuBackgroundColor.copy(alpha = 0.3f) else menuBackgroundColor
                    )
                )
            }
        }
    }
}
