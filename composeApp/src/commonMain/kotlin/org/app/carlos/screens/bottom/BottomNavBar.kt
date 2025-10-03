package org.app.carlos.screens.bottom

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import carlosapp.composeapp.generated.resources.Res
import carlosapp.composeapp.generated.resources.default_favorites
import carlosapp.composeapp.generated.resources.default_favorites_selected
import carlosapp.composeapp.generated.resources.default_history
import carlosapp.composeapp.generated.resources.default_history_selected
import carlosapp.composeapp.generated.resources.default_home
import carlosapp.composeapp.generated.resources.default_home_selected
import carlosapp.composeapp.generated.resources.default_search
import carlosapp.composeapp.generated.resources.default_search_selected
import carlosapp.composeapp.generated.resources.default_statistics
import carlosapp.composeapp.generated.resources.default_statistics_selected
import carlosapp.composeapp.generated.resources.marine_favorites
import carlosapp.composeapp.generated.resources.marine_favorites_selected
import carlosapp.composeapp.generated.resources.marine_history
import carlosapp.composeapp.generated.resources.marine_history_selected
import carlosapp.composeapp.generated.resources.marine_home
import carlosapp.composeapp.generated.resources.marine_home_selected
import carlosapp.composeapp.generated.resources.marine_search
import carlosapp.composeapp.generated.resources.marine_search_selected
import carlosapp.composeapp.generated.resources.marine_statistics
import carlosapp.composeapp.generated.resources.marine_statistics_selected
import carlosapp.composeapp.generated.resources.midnight_favorites
import carlosapp.composeapp.generated.resources.midnight_favorites_selected
import carlosapp.composeapp.generated.resources.midnight_history
import carlosapp.composeapp.generated.resources.midnight_history_selected
import carlosapp.composeapp.generated.resources.midnight_home
import carlosapp.composeapp.generated.resources.midnight_home_selected
import carlosapp.composeapp.generated.resources.midnight_search
import carlosapp.composeapp.generated.resources.midnight_search_selected
import carlosapp.composeapp.generated.resources.midnight_statistics
import carlosapp.composeapp.generated.resources.midnight_statistics_selected
import carlosapp.composeapp.generated.resources.solaris_favorites
import carlosapp.composeapp.generated.resources.solaris_favorites_selected
import carlosapp.composeapp.generated.resources.solaris_history
import carlosapp.composeapp.generated.resources.solaris_history_selected
import carlosapp.composeapp.generated.resources.solaris_home
import carlosapp.composeapp.generated.resources.solaris_home_selected
import carlosapp.composeapp.generated.resources.solaris_search
import carlosapp.composeapp.generated.resources.solaris_search_selected
import carlosapp.composeapp.generated.resources.solaris_statistics
import carlosapp.composeapp.generated.resources.solaris_statistics_selected
import org.app.carlos.screens.Screen
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun BottomNavBar(
    navController: NavHostController,
    currentRoute: String,
    selectedThemeId: String?
) {
    val items = listOf(
        Screen.Home,
        Screen.Search,
        Screen.History,
        Screen.Statistics,
        Screen.Favorites
    )

    val containerColor = when (selectedThemeId) {
        "default" -> Color(0xFF152470)
        "midnight" -> Color(0xFF090733)
        "solaris" -> Color(0xFFFFFDF4)
        "marine" -> Color(0xFF0A0A0A)
        else -> Color.White
    }

    NavigationBar(
        containerColor = containerColor
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
                    val iconRes = getIconForScreen(screen, selectedThemeId, selected)
                    Image(
                        painter = painterResource(iconRes),
                        contentDescription = screen.route,
                        modifier = Modifier.size(60.dp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Unspecified,
                    unselectedIconColor = Color.Unspecified,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

fun getIconForScreen(screen: Screen, themeId: String?, selected: Boolean): DrawableResource {
    return when(themeId) {
        "default" -> when(screen) {
            Screen.Home -> if (selected) Res.drawable.default_home_selected else Res.drawable.default_home
            Screen.Search -> if (selected) Res.drawable.default_search_selected else Res.drawable.default_search
            Screen.History -> if (selected) Res.drawable.default_history_selected else Res.drawable.default_history
            Screen.Statistics -> if (selected) Res.drawable.default_statistics_selected else Res.drawable.default_statistics
            Screen.Favorites -> if (selected) Res.drawable.default_favorites_selected else Res.drawable.default_favorites
            else -> Res.drawable.default_home
        }
        "midnight" -> when(screen) {
            Screen.Home -> if (selected) Res.drawable.midnight_home_selected else Res.drawable.midnight_home
            Screen.Search -> if (selected) Res.drawable.midnight_search_selected else Res.drawable.midnight_search
            Screen.History -> if (selected) Res.drawable.midnight_history_selected else Res.drawable.midnight_history
            Screen.Statistics -> if (selected) Res.drawable.midnight_statistics_selected else Res.drawable.midnight_statistics
            Screen.Favorites -> if (selected) Res.drawable.midnight_favorites_selected else Res.drawable.midnight_favorites
            else -> Res.drawable.midnight_home
        }
        "solaris" -> when(screen) {
            Screen.Home -> if (selected) Res.drawable.solaris_home_selected else Res.drawable.solaris_home
            Screen.Search -> if (selected) Res.drawable.solaris_search_selected else Res.drawable.solaris_search
            Screen.History -> if (selected) Res.drawable.solaris_history_selected else Res.drawable.solaris_history
            Screen.Statistics -> if (selected) Res.drawable.solaris_statistics_selected else Res.drawable.solaris_statistics
            Screen.Favorites -> if (selected) Res.drawable.solaris_favorites_selected else Res.drawable.solaris_favorites
            else -> Res.drawable.solaris_home
        }
        "marine" -> when(screen) {
            Screen.Home -> if (selected) Res.drawable.marine_home_selected else Res.drawable.marine_home
            Screen.Search -> if (selected) Res.drawable.marine_search_selected else Res.drawable.marine_search
            Screen.History -> if (selected) Res.drawable.marine_history_selected else Res.drawable.marine_history
            Screen.Statistics -> if (selected) Res.drawable.marine_statistics_selected else Res.drawable.marine_statistics
            Screen.Favorites -> if (selected) Res.drawable.marine_favorites_selected else Res.drawable.marine_favorites
            else -> Res.drawable.marine_home
        }
        else -> when(screen) {
            Screen.Home -> if (selected) Res.drawable.default_home_selected else Res.drawable.default_home
            Screen.Search -> if (selected) Res.drawable.default_search_selected else Res.drawable.default_search
            Screen.History -> if (selected) Res.drawable.default_history_selected else Res.drawable.default_history
            Screen.Statistics -> if (selected) Res.drawable.default_statistics_selected else Res.drawable.default_statistics
            Screen.Favorites -> if (selected) Res.drawable.default_favorites_selected else Res.drawable.default_favorites
            else -> Res.drawable.default_home
        }
    }
}