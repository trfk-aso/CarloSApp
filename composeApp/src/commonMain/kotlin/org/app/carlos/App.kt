package org.app.carlos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import carlosapp.composeapp.generated.resources.Res
import carlosapp.composeapp.generated.resources.compose_multiplatform
import org.app.carlos.screens.Screen
import org.app.carlos.screens.addEdit.AddEditExpenseScreen
import org.app.carlos.screens.details.ExpenseDetailsScreen
import org.app.carlos.screens.favorites.FavoritesScreen
import org.app.carlos.screens.history.HistoryScreen
import org.app.carlos.screens.home.HomeScreen
import org.app.carlos.screens.list.ListScreen
import org.app.carlos.screens.onboarding.OnboardingScreen
import org.app.carlos.screens.search.SearchScreen
import org.app.carlos.screens.splash.SplashScreen
import org.app.carlos.viewModel.AddEditExpenseViewModel
import org.app.carlos.viewModel.ExpenseDetailsViewModel
import org.app.carlos.viewModel.HomeViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun App() {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
                startDestination = Screen.Splash.route
        ) { composable(Screen.Splash.route) { SplashScreen(navController) }
            composable(Screen.Home.route) { HomeScreen(navController) }
            composable(Screen.Onboarding.route) { OnboardingScreen(navController) }
            composable(
                route = Screen.AddEditExpense.route + "?expenseId={expenseId}&fromTemplate={fromTemplate}",
                arguments = listOf(
                    navArgument("expenseId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    },
                    navArgument("fromTemplate") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) { backStackEntry ->
                val viewModel: AddEditExpenseViewModel = koinViewModel(
                    parameters = { parametersOf(backStackEntry.savedStateHandle) }
                )

                AddEditExpenseScreen(
                    navController = navController,
                    viewModel = viewModel,
                    homeViewModel = koinInject()
                )
            }
            composable(Screen.Search.route) { SearchScreen(navController) }
            composable(
                route = "${Screen.Details.route}/{expenseId}",
                arguments = listOf(navArgument("expenseId") { type = NavType.LongType })
            ) { backStackEntry ->
                val viewModel: ExpenseDetailsViewModel = koinViewModel(
                    parameters = { parametersOf(backStackEntry.savedStateHandle) }
                )
                ExpenseDetailsScreen(navController, viewModel)
            }
            composable(Screen.List.route) { ListScreen(navController)  }
            composable(Screen.Favorites.route) { FavoritesScreen(navController) }
            composable(Screen.History.route) { HistoryScreen(navController) }

        }
}
