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
import androidx.compose.ui.unit.dp
import kotlin.math.pow
import kotlin.math.round

@Composable
fun AveragesSection(avgPerMonth: Double, averagesByCategory: Map<String, Double>) {
    Column(Modifier.fillMaxWidth()) {
        Card(
            Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2563EB))
        ) {
            Text(
                "Average per month: $${avgPerMonth.toDecimalString(2)}",
                Modifier.padding(16.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
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
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2563EB))
                ) {
                    Column(
                        Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Average $cat", color = Color.White)
                        Text(
                            "$${avg.toDecimalString(2)}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
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