package org.app.carlos

import androidx.compose.ui.window.ComposeUIViewController
import org.app.carlos.di.initKoin

fun MainViewController() = ComposeUIViewController {
    initKoin {  }
    App()
}