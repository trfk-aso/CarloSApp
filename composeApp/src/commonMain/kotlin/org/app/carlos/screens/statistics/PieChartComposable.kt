package org.app.carlos.screens.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PieChartComposable(
    data: Map<String, Double>,
    colors: Map<String, Color> = mapOf(
        "Fuel" to Color(0xFFFFC107),
        "Insurance" to Color(0xFFE91E63),
        "Repair" to Color(0xFF3F51B5),
        "Other" to Color(0xFF9C27B0)
    )
) {
    val total = data.values.sum()
    val proportions = data.mapValues { if (total == 0.0) 0f else (it.value / total).toFloat() }

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Canvas(modifier = Modifier.size(160.dp)) {
            var startAngle = -90f
            proportions.forEach { (category, proportion) ->
                val sweep = proportion * 360f
                drawArc(
                    color = colors[category] ?: Color.Gray,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = true
                )
                startAngle += sweep
            }
        }

        Column {
            data.forEach { (category, amount) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(12.dp)
                            .background(colors[category] ?: Color.Gray, CircleShape)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("$category: $${amount.toInt()} (${((amount / total) * 100).toInt()}%)", color = Color.White)
                }
            }
        }
    }
}
