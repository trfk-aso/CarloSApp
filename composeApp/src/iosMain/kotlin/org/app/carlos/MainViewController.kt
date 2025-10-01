package org.app.carlos

import androidx.compose.ui.window.ComposeUIViewController
import org.app.carlos.di.initKoin

import androidx.compose.ui.window.ComposeUIViewController

import androidx.compose.runtime.Composable
import org.app.carlos.billing.IOSBillingRepository
import org.app.carlos.data.repository.BillingRepository
import org.app.carlos.data.repository.ThemeRepository
import org.app.carlos.data.repository.ThemeRepositoryImpl
import org.koin.compose.getKoin
import org.koin.compose.koinInject
import org.koin.core.context.startKoin
import org.koin.dsl.module
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {

    startKoin {
        modules(
            module {
                single<ThemeRepository> { ThemeRepositoryImpl(get()) }
                single<BillingRepository> { IOSBillingRepository(get()) }
            }
        )
    }

    return ComposeUIViewController {
        val themeRepository: ThemeRepository = getKoin().get()
        val billingRepository: BillingRepository = getKoin().get()
        App(themeRepository, billingRepository)
    }
}
