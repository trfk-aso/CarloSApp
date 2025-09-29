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
import org.app.carlos.screens.Screen
import kotlin.math.round

@Composable
fun SwipeToDismissItem(
    expense: Expense,
    navController: NavHostController,
    onDelete: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    val animatedOffsetX by animateFloatAsState(targetValue = offsetX)

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
                                println("Editing expense, id: $idToEdit")

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
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .background(Color(0xFF2563EB), RoundedCornerShape(12.dp))
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
                Icon(icon, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        expense.title?.takeIf { it.isNotBlank() } ?: expense.category,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        expense.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF93C5FD)
                    )
                }
            }
            Text(
                formatAmount(expense.amount),
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

fun formatAmount(amount: Double): String {
    val rounded = round(amount * 100) / 100
    return "$$rounded"
}