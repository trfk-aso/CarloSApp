package org.app.carlos.screens.list

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.app.carlos.data.model.Expense
import org.app.carlos.data.model.Theme
import org.app.carlos.screens.Screen
import org.app.carlos.viewModel.ThemeUiState
import kotlin.math.round

@Composable
fun SwipeToDismissItem(
    expense: Expense,
    navController: NavHostController,
    onDelete: () -> Unit,
    selectedTheme: ThemeUiState?
) {
    var offsetX by remember { mutableStateOf(0f) }
    val animatedOffsetX by animateFloatAsState(targetValue = offsetX)

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

    val textColor = when (selectedTheme?.id) {
        "solaris" -> Color.Black
        else -> Color.White
    }

    Box(
        Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        when {
                            offsetX < -150f -> onDelete()
                            offsetX > 150f -> {
                                val idToEdit = expense.id ?: return@detectHorizontalDragGestures
                                navController.navigate("${Screen.AddEditExpense.route}?expenseId=$idToEdit") {
                                    launchSingleTop = true
                                }
                            }
                        }
                        offsetX = 0f
                    }
                ) { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount
                }
            }
    ) {

        Row(
            Modifier
                .matchParentSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFF22C55E), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White)
            }
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFFEF4444), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(animatedOffsetX.toInt(), 0) }
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .background(rowBackground, RoundedCornerShape(12.dp))
                .clickable {
                    navController.navigate(Screen.Details.createRoute(expense.id!!))
                }
                .padding(16.dp),
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
                        .background(iconBackground, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color.White)
                }

                Spacer(Modifier.width(8.dp))

                Column {
                    Text(
                        expense.title?.takeIf { it.isNotBlank() } ?: expense.category,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Text(
                        expense.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor.copy(alpha = 0.7f)
                    )
                }
            }
            Text(
                formatAmount(expense.amount),
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

fun formatAmount(amount: Double): String {
    val rounded = round(amount * 100) / 100
    return "$$rounded"
}