package org.app.carlos.screens.calenda

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.*

@Composable
fun CalendarView(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    selectedThemeId: String? = "default"
) {
    var currentMonth by remember { mutableStateOf(Clock.System.todayIn(TimeZone.currentSystemDefault())) }

    val backgroundColor = when (selectedThemeId) {
        "default" -> Color(0xFF1B2D8A)
        "midnight" -> Color(0xFF1D1B49)
        "solaris" -> Color(0xFFFFE8A5)
        "marine" -> Color(0xFF22272E)
        else -> Color(0xFF1B2D8A)
    }

    val selectedTextColor = when (selectedThemeId) {
        "default", "solaris", "marine" -> Color.Black
        else -> Color.White
    }

    val textColor = when (selectedThemeId) {
        "default", "midnight", "marine" -> Color.White
        "solaris" -> Color.Black
        else -> Color.White
    }

    val selectedDayColor = when (selectedThemeId) {
        "default" -> Color(0xFFFFF315)
        "midnight" -> Color(0xFFB421FF)
        "solaris" -> Color(0xFFFFC654)
        "marine" -> Color(0xFF37FFE6)
        else -> Color(0xFFFFF315)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { currentMonth = currentMonth.minus(DatePeriod(months = 1)) }) {
                Text("←", color = textColor, fontSize = 20.sp)
            }
            Text(
                "${currentMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${currentMonth.year}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = textColor
            )
            TextButton(onClick = { currentMonth = currentMonth.plus(DatePeriod(months = 1)) }) {
                Text("→", color = textColor, fontSize = 20.sp)
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach {
                Text(
                    it,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = textColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        val firstDayOfMonth = LocalDate(currentMonth.year, currentMonth.monthNumber, 1)
        val firstWeekDay = firstDayOfMonth.dayOfWeek.isoDayNumber
        val daysInMonth = getDaysInMonth(currentMonth.year, currentMonth.month)

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(300.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            items(firstWeekDay - 1) { Box(Modifier.size(40.dp)) }

            items(daysInMonth) { index ->
                val day = index + 1
                val date = LocalDate(currentMonth.year, currentMonth.monthNumber, day)
                val isSelected = date == selectedDate

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(if (isSelected) selectedDayColor else backgroundColor)
                        .clickable { onDateSelected(date) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$day",
                        color = if (isSelected) selectedTextColor else textColor,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

fun getDaysInMonth(year: Int, month: Month): Int =
    when (month) {
        Month.JANUARY, Month.MARCH, Month.MAY, Month.JULY,
        Month.AUGUST, Month.OCTOBER, Month.DECEMBER -> 31
        Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
        Month.FEBRUARY -> if (isLeapYear(year)) 29 else 28
        else -> error("Unexpected month: $month")
    }


fun isLeapYear(year: Int): Boolean =
    (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)