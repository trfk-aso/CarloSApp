package org.app.carlos.screens.favorites

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.app.carlos.data.model.Expense
import org.app.carlos.screens.Screen
import org.app.carlos.screens.list.formatAmount

@Composable
fun SwipeToDismissFavoriteItem(
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
                Icon(Icons.Default.Edit, contentDescription = "Edit Template", tint = Color.White)
            }
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFFEF4444), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Template", tint = Color.White)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(animatedOffsetX.toInt(), 0) }
                .clickable {
                    navController.navigate("${Screen.AddEditExpense.route}?fromTemplate=${expense.id}")

                }
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(Color(0xFF2563EB), RoundedCornerShape(12.dp))
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
                        expense.title?.takeIf { it.isNotBlank() } ?: "Template",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        listOfNotNull(expense.category, expense.notes?.takeIf { it.isNotBlank() })
                            .joinToString(" • "),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFBFDBFE)
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Favorite Template",
                tint = Color(0xFFFFD700)
            )
        }
    }
}