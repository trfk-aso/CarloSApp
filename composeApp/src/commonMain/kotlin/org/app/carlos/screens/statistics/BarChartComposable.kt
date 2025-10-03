package org.app.carlos.screens.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Month
import org.app.carlos.viewModel.ThemeUiState

@Composable
fun BarChartComposable(
    data: Map<Int, Double>,
    onBarClick: (Int) -> Unit,
    selectedTheme: ThemeUiState?
) {
    val max = (data.values.maxOrNull() ?: 0.0).coerceAtLeast(1.0)
    val step = (max / 5).let { if (it < 1) 1.0 else it }
    val yLabels = (0..5).map { (it * step).toInt() }

    val monthNames = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    val backgroundColor = when (selectedTheme?.id) {
        "default" -> Color(0xFF1B2D8A)
        "midnight" -> Color(0xFF1D1B49)
        "solaris" -> Color(0xFFFFE8A5)
        "marine" -> Color(0xFF22272E)
        else -> Color(0xFF1B2D8A)
    }

    val yLabelColor = when (selectedTheme?.id) {
        "default", "midnight", "marine" -> Color.White.copy(alpha = 0.7f)
        "solaris" -> Color.Black.copy(alpha = 0.7f)
        else -> Color.White.copy(alpha = 0.7f)
    }

    val titleColor = when (selectedTheme?.id) {
        "default", "midnight", "marine" -> Color.White
        "solaris" -> Color.Black
        else -> Color.White
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Expense", color = titleColor, fontSize = 14.sp)
            Text("2025", color = titleColor, fontSize = 14.sp)
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                yLabels.reversed().forEach { value ->
                    Text(
                        text = value.toString(),
                        fontSize = 10.sp,
                        color = yLabelColor
                    )
                }
            }

            Spacer(Modifier.width(6.dp))

            Box(
                modifier = Modifier.weight(1f)
            ) {
                Canvas(Modifier.matchParentSize()) {
                    val stepY = size.height / yLabels.size
                    repeat(yLabels.size) { i ->
                        val y = stepY * i
                        drawLine(
                            color = titleColor.copy(alpha = 0.2f),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    data.forEach { (month, value) ->
                        val heightFraction = (value / (yLabels.maxOrNull() ?: 1)).toFloat()
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { onBarClick(month) }
                                .weight(1f, false)
                        ) {
                            val barColor = if (selectedTheme?.id == "solaris") Color(0xFFFFCC4A) else Color(0xFF00C2FF)

                            Box(
                                modifier = Modifier
                                    .height((180.dp * heightFraction).coerceAtLeast(4.dp))
                                    .width(18.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(barColor)
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = if (month == 0) "All" else monthNames.getOrNull(month - 1) ?: "???",
                                fontSize = 11.sp,
                                color = titleColor
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text("Months", color = titleColor, fontSize = 12.sp)
        }
    }
}