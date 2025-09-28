package org.app.carlos.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavHostController
import carlosapp.composeapp.generated.resources.Res
import carlosapp.composeapp.generated.resources.splash_background
import org.app.carlos.screens.Screen
import org.app.carlos.viewModel.SplashViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@Composable
fun SplashScreen(
    navController: NavHostController,
    viewModel: SplashViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isFirstLaunch) {
        viewModel.splashDelay(1L).collect {
            if (uiState.isFirstLaunch) {
                navController.navigate(Screen.Onboarding.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
                viewModel.markLaunched()
            } else {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
        }
    }

    Image(
        painter = painterResource(Res.drawable.splash_background),
        contentDescription = "Splash Background",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}