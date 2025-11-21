package org.app.carlos.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavHostController
import carlosapp.composeapp.generated.resources.Res
import carlosapp.composeapp.generated.resources.splash_background
import carlosapp.composeapp.generated.resources.splash_marine
import carlosapp.composeapp.generated.resources.splash_midnight
import carlosapp.composeapp.generated.resources.splash_solaris
import org.app.carlos.screens.Screen
import org.app.carlos.viewModel.SettingsViewModel
import org.app.carlos.viewModel.SplashUiState
import org.app.carlos.viewModel.SplashViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@Composable
fun SplashScreen(
    navController: NavHostController,
    splashViewModel: SplashViewModel = koinInject(),
    settingsViewModel: SettingsViewModel
) {
    val splashState by splashViewModel.uiState.collectAsState()
    val settingsState by settingsViewModel.uiState.collectAsState()

    val selectedTheme = settingsState.themes.firstOrNull { it.isSelected }
    val backgroundRes = when (selectedTheme?.id) {
        "default" -> Res.drawable.splash_background
        "midnight" -> Res.drawable.splash_midnight
        "solaris" -> Res.drawable.splash_solaris
        "marine" -> Res.drawable.splash_marine
        else -> Res.drawable.splash_background
    }

    LaunchedEffect(Unit) {
        snapshotFlow { splashState }
            .collect { state ->

                when (state) {

                    is SplashUiState.ShowWeb -> {
                        val url = state.url
                        navController.navigate(Screen.Web.route + "/$url") {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }

                    SplashUiState.ShowApp -> {
                        splashViewModel.splashDelay(1L).collect {

                            if (splashViewModel.isFirstLaunch()) {
                                navController.navigate(Screen.Onboarding.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                                splashViewModel.markLaunched()
                            } else {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            }
                        }
                    }

                    else -> Unit
                }
            }
    }

    Image(
        painter = painterResource(backgroundRes),
        contentDescription = "Splash Background",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}