package org.app.carlos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.app.carlos.billing.AndroidBillingRepository
import org.app.carlos.data.repository.BillingRepository
import org.app.carlos.data.repository.ThemeRepository
import org.koin.mp.KoinPlatform.getKoin

class MainActivity : ComponentActivity() {

    private lateinit var billingRepository: BillingRepository
    private lateinit var themeRepository: ThemeRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        themeRepository = getKoin().get()

        billingRepository = AndroidBillingRepository(
            context = this,
            themeRepository = themeRepository
        )

        lifecycleScope.launch {
            themeRepository.initializeThemes()
        }

        setContent {
            App(
                themeRepository = themeRepository,
                billingRepository = billingRepository
            )
        }
    }

    private fun enableEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}