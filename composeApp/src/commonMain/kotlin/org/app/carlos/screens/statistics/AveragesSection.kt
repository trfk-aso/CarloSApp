package org.app.carlos.screens.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.app.carlos.viewModel.ThemeUiState
import kotlin.math.pow
import kotlin.math.round

@Composable
fun AveragesSection(
    avgPerMonth: Double,
    averagesByCategory: Map<String, Double>,
    selectedTheme: ThemeUiState?
) {
    val cardBackground = when (selectedTheme?.id) {
        "default" -> Color(0xFF3E5CFF)
        "midnight" -> Color(0xFF2C387B)
        "solaris" -> Color(0xFFFFDCA5)
        "marine" -> Color(0xFF22272E)
        else -> Color(0xFF3E5CFF)
    }

    val textColor = when (selectedTheme?.id) {
        "default", "midnight", "marine" -> Color.White
        "solaris" -> Color.Black
        else -> Color.White
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = cardBackground)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Average per month: $${avgPerMonth.toDecimalString(2)}",
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        FlowRow(
            maxItemsInEachRow = 2,
            modifier = Modifier.fillMaxWidth()
        ) {
            averagesByCategory.forEach { (cat, avg) ->
                Card(
                    Modifier
                        .padding(4.dp)
                        .weight(1f),
                    colors = CardDefaults.cardColors(containerColor = cardBackground)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Average $cat",
                            color = textColor,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "$${avg.toDecimalString(2)}",
                            color = textColor,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

fun Double.toDecimalString(digits: Int = 2): String {
    val factor = 10.0.pow(digits)
    val rounded = round(this * factor) / factor
    return buildString {
        append(rounded)
        if (!contains('.')) append(".00")
        else {
            val parts = split('.')
            if (parts[1].length < digits) {
                append("0".repeat(digits - parts[1].length))
            }
        }
    }
}