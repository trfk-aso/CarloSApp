package org.app.carlos.screens.bottom

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import org.app.carlos.screens.Screen

@Composable
fun BottomNavBar(navController: NavHostController, currentRoute: String) {
    val items = listOf(
        Screen.Home,
        Screen.Search,
        Screen.History,
        Screen.Statistics,
        Screen.Favorites
    )

    NavigationBar(
        containerColor = Color.White
    ) {
        items.forEach { screen ->
            val selected = currentRoute == screen.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    val icon = when (screen) {
                        Screen.Home -> Icons.Default.Home
                        Screen.Search -> Icons.Default.Search
                        Screen.History -> Icons.Default.History
                        Screen.Statistics -> Icons.Default.PieChart
                        Screen.Favorites -> Icons.Default.Star
                        else -> Icons.Default.MoreHoriz
                    }

                    Icon(
                        imageVector = icon,
                        contentDescription = screen.route,
                        tint = if (selected) Color(0xFFFFD700) else Color.Black
                    )
                }
            )
        }
    }
}