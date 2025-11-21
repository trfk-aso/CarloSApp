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
import org.app.carlos.data.repository.BillingRepository
import org.app.carlos.data.repository.ExpenseRepository
import org.app.carlos.data.repository.SettingsRepository
import org.app.carlos.data.repository.ThemeRepository
import org.app.carlos.screens.LoadingScreen
import org.app.carlos.screens.Screen
import org.app.carlos.screens.about.AboutScreen
import org.app.carlos.screens.addEdit.AddEditExpenseScreen
import org.app.carlos.screens.details.ExpenseDetailsScreen
import org.app.carlos.screens.favorites.FavoritesScreen
import org.app.carlos.screens.history.HistoryScreen
import org.app.carlos.screens.home.HomeScreen
import org.app.carlos.screens.list.ListScreen
import org.app.carlos.screens.onboarding.OnboardingScreen
import org.app.carlos.screens.search.SearchScreen
import org.app.carlos.screens.settings.SettingsScreen
import org.app.carlos.screens.splash.SplashScreen
import org.app.carlos.screens.statistics.StatisticsScreen
import org.app.carlos.viewModel.AddEditExpenseViewModel
import org.app.carlos.viewModel.ExpenseDetailsViewModel
import org.app.carlos.viewModel.FavoritesViewModel
import org.app.carlos.viewModel.HistoryViewModel
import org.app.carlos.viewModel.HomeViewModel
import org.app.carlos.viewModel.SearchViewModel
import org.app.carlos.viewModel.SettingsViewModel
import org.app.carlos.viewModel.SplashUiState
import org.app.carlos.viewModel.SplashViewModel
import org.app.carlos.viewModel.StatisticsViewModel
import org.koin.compose.getKoin
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun App() {
    val splashViewModel: SplashViewModel = koinInject()
    val uiState by splashViewModel.uiState.collectAsState()

    when (val state = uiState) {

        SplashUiState.Loading -> LoadingScreen()

        is SplashUiState.ShowWeb -> {
            if(state.url.isBlank()) {
                LoadingScreen()
            } else {
                WebScreen(url = state.url)
            }
        }

        SplashUiState.ShowApp -> MainAppNavigation()
    }
}

@Composable
fun MainAppNavigation() {
    val navController = rememberNavController()
    val repository: ExpenseRepository = koinInject()
    val settingsRepository: SettingsRepository = koinInject()
    val homeViewModel: HomeViewModel = koinInject()
    val favoritesViewModel: FavoritesViewModel = koinInject()
    val historyViewModel: HistoryViewModel = koinInject()
    val searchViewModel: SearchViewModel = koinInject()
    val statisticsViewModel: StatisticsViewModel = koinInject()
    val themeRepository: ThemeRepository = koinInject()
    val billingRepository: BillingRepository = koinInject()

    val settingsViewModel = remember {
        SettingsViewModel(
            repository = repository,
            settingsRepository = settingsRepository,
            themeRepository = themeRepository,
            billingRepository = billingRepository
        )
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController, koinInject(), settingsViewModel)
        }
        composable(Screen.Home.route) { HomeScreen(navController, homeViewModel, settingsViewModel) }
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
                homeViewModel = koinInject(),
                settingsViewModel = settingsViewModel
            )
        }
        composable(Screen.Search.route) { SearchScreen(navController, searchViewModel, settingsViewModel) }
        composable(
            route = "${Screen.Details.route}/{expenseId}",
            arguments = listOf(navArgument("expenseId") { type = NavType.LongType })
        ) { backStackEntry ->
            val viewModel: ExpenseDetailsViewModel = koinViewModel(
                parameters = { parametersOf(backStackEntry.savedStateHandle) }
            )
            ExpenseDetailsScreen(
                navController,
                viewModel,
                homeViewModel,
                searchViewModel,
                favoritesViewModel,
                historyViewModel,
                statisticsViewModel,
                settingsViewModel
            )
        }
        composable(Screen.List.route) { ListScreen(navController, searchViewModel, homeViewModel, settingsViewModel) }
        composable(Screen.Favorites.route) { FavoritesScreen(navController, favoritesViewModel, settingsViewModel) }
        composable(Screen.History.route) { HistoryScreen(navController, historyViewModel, homeViewModel, statisticsViewModel, settingsViewModel) }
        composable(Screen.Statistics.route) { StatisticsScreen(navController, statisticsViewModel, homeViewModel, searchViewModel, settingsViewModel) }
        composable(Screen.Settings.route) {
            SettingsScreen(
                navController = navController,
                viewModel = settingsViewModel,
                themeRepository = themeRepository
            )
        }
        composable(Screen.About.route) { AboutScreen(navController, settingsViewModel) }
    }
}
