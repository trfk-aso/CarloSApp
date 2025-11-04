package org.app.carlos.screens.favorites

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import carlosapp.composeapp.generated.resources.Res
import carlosapp.composeapp.generated.resources.bg_default
import carlosapp.composeapp.generated.resources.bg_marine
import carlosapp.composeapp.generated.resources.bg_midnight
import carlosapp.composeapp.generated.resources.bg_solaris
import carlosapp.composeapp.generated.resources.empty_default_favorites
import carlosapp.composeapp.generated.resources.empty_marine_favorites
import carlosapp.composeapp.generated.resources.empty_midnight_favorites
import carlosapp.composeapp.generated.resources.empty_solaris_favorites
import org.app.carlos.screens.Screen
import org.app.carlos.screens.bottom.BottomNavBar
import org.app.carlos.screens.list.SwipeToDismissItem
import org.app.carlos.viewModel.FavoritesViewModel
import org.app.carlos.viewModel.SettingsViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    navController: NavHostController,
    viewModel: FavoritesViewModel = koinInject(),
    settingsViewModel: SettingsViewModel
) {
    val favorites by viewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route
    val settingsState by settingsViewModel.uiState.collectAsState()
    val selectedTheme = settingsState.themes.firstOrNull { it.isSelected }

    val backgroundRes = when (selectedTheme?.id) {
        "default" -> Res.drawable.bg_default
        "midnight" -> Res.drawable.bg_midnight
        "solaris" -> Res.drawable.bg_solaris
        "marine" -> Res.drawable.bg_marine
        else -> Res.drawable.bg_default
    }
    val overlayAlpha = if (selectedTheme?.id == "solaris") 0f else 0.4f

    val textColor = when (selectedTheme?.id) {
        "default" -> Color.White
        "midnight" -> Color.White
        "solaris" -> Color.Black
        "marine" -> Color.White
        else -> Color.White
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(backgroundRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = overlayAlpha))
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Favorites", fontWeight = FontWeight.Bold, color = textColor) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            bottomBar = {
                BottomNavBar(
                    navController = navController,
                    currentRoute = currentRoute,
                    selectedThemeId = selectedTheme?.id
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            if (favorites.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val emptyImageRes = when (selectedTheme?.id) {
                        "default" -> Res.drawable.empty_default_favorites
                        "midnight" -> Res.drawable.empty_midnight_favorites
                        "solaris" -> Res.drawable.empty_solaris_favorites
                        "marine" -> Res.drawable.empty_marine_favorites
                        else -> Res.drawable.empty_default_favorites
                    }

                    Image(
                        painter = painterResource(emptyImageRes),
                        contentDescription = "Empty state",
                        modifier = Modifier.size(300.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    items(favorites, key = { it.id!! }) { expense ->
                        SwipeToDismissFavoriteItem(
                            expense = expense,
                            navController = navController,
                            onDelete = { viewModel.deleteFavorite(expense) },
                            selectedTheme = selectedTheme
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}