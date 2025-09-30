package org.app.carlos.screens.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.app.carlos.viewModel.PeriodType
import org.app.carlos.viewModel.YearMonth

@Composable
fun PeriodSelector(
    selectedPeriod: PeriodType,
    selectedMonth: YearMonth?,
    selectedYear: Int?,
    onPeriodChange: (PeriodType) -> Unit,
    onMonthChange: (YearMonth) -> Unit,
    onYearChange: (Int) -> Unit
) {
    Column {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            PeriodType.values().forEach { type ->
                TextButton(
                    onClick = { onPeriodChange(type) },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (type == selectedPeriod) Color.White else Color.LightGray
                    )
                ) {
                    Text(type.name)
                }
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
                    onItemSelected = { onMonthChange(it) }
                )
            }
            PeriodType.YEAR -> {
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                val years = (now.year - 5..now.year).toList()

                DropdownSelector(
                    items = years,
                    selectedItem = selectedYear ?: now.year,
                    label = { it.toString() },
                    onItemSelected = { onYearChange(it) }
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
    onItemSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(onClick = { expanded = true }) {
            Text(selectedItem?.let(label) ?: "Select")
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(label(item)) },
                    onClick = {
                        expanded = false
                        onItemSelected(item)
                    }
                )
            }
        }
    }
}
