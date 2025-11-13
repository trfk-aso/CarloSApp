package org.app.carlos.screens.statistics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.text.font.FontWeight
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

    val (backgroundColor, yLabelColor, titleColor, barGradient) = when (selectedTheme?.id) {
        "default" -> Quad(
            Color(0xFF1B2D8A),
            Color.White.copy(alpha = 0.6f),
            Color.White,
            Brush.verticalGradient(listOf(Color(0xFF4D9FFF), Color(0xFF0063FF)))
        )
        "midnight" -> Quad(
            Color(0xFF1D1B49),
            Color.White.copy(alpha = 0.6f),
            Color.White,
            Brush.verticalGradient(listOf(Color(0xFF7B6AFF), Color(0xFF3A2CC9)))
        )
        "solaris" -> Quad(
            Color(0xFFFFE8A5),
            Color.Black.copy(alpha = 0.6f),
            Color.Black,
            Brush.verticalGradient(listOf(Color(0xFFFFD76A), Color(0xFFFFAC33)))
        )
        "marine" -> Quad(
            Color(0xFF22272E),
            Color.White.copy(alpha = 0.6f),
            Color.White,
            Brush.verticalGradient(listOf(Color(0xFF37FFE6), Color(0xFF007F8C)))
        )
        else -> Quad(
            Color(0xFF1B2D8A),
            Color.White.copy(alpha = 0.6f),
            Color.White,
            Brush.verticalGradient(listOf(Color(0xFF4D9FFF), Color(0xFF0063FF)))
        )
    }

    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(data) {
        animatedProgress.snapTo(0f)
        animatedProgress.animateTo(1f, animationSpec = tween(1000, easing = FastOutSlowInEasing))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Expense", color = titleColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text("2025", color = titleColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
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

            Spacer(Modifier.width(8.dp))

            Box(modifier = Modifier.weight(1f)) {
                Canvas(Modifier.matchParentSize()) {
                    val stepY = size.height / yLabels.size
                    repeat(yLabels.size) { i ->
                        val y = stepY * i
                        drawLine(
                            color = titleColor.copy(alpha = 0.15f),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    data.forEach { (month, value) ->
                        val heightFraction = ((value / max).toFloat() * animatedProgress.value).coerceIn(0f, 1f)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier
                                .clickable { onBarClick(month) }
                                .weight(1f, false)
                        ) {
                            AnimatedVisibility(visible = animatedProgress.value > 0.7f) {
                                Text(
                                    text = "${value.toInt()}",
                                    fontSize = 11.sp,
                                    color = titleColor.copy(alpha = 0.9f),
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .height((180.dp * heightFraction).coerceAtLeast(6.dp))
                                    .width(22.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(barGradient)
                                    .shadow(5.dp, RoundedCornerShape(8.dp))
                            )

                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = if (month == 0) "All" else monthNames.getOrNull(month - 1) ?: "?",
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

data class Quad<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
operator fun <A, B, C, D> Quad<A, B, C, D>.component1() = a
operator fun <A, B, C, D> Quad<A, B, C, D>.component2() = b
operator fun <A, B, C, D> Quad<A, B, C, D>.component3() = c
operator fun <A, B, C, D> Quad<A, B, C, D>.component4() = d