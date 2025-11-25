package org.app.carlos.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.app.carlos.data.repository.RemoteConfigRepository
import org.app.carlos.data.repository.SettingsRepository

class SplashViewModel(
    private val settings: SettingsRepository,
    private val remoteConfig: RemoteConfigRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState

    init {
        viewModelScope.launch {
            println("🟦 [Splash] ViewModel init → starting startup logic")
            runStartupLogic()
        }
    }

    private suspend fun runStartupLogic() {

        val saved = remoteConfig.getSavedUrl()
        println("🟣 [Splash] Step 1: savedUrl = $saved")

        if (!saved.isNullOrBlank()) {
            println("🟢 [Splash] Saved URL FOUND → show web → $saved")

            delay(100)
            _uiState.value = SplashUiState.ShowWeb(saved)
            return
        }

        val isFirst = settings.isFirstLaunch()
        println("🔵 [Splash] Step 2: isFirstLaunch = $isFirst")

        if (isFirst) {

            val fetchedUrl = remoteConfig.fetchUrl()
            println("🟣 [Splash] fetchedUrl = $fetchedUrl")

            if (!fetchedUrl.isNullOrBlank() && fetchedUrl.startsWith("http")) {

                remoteConfig.saveUrl(fetchedUrl)
                settings.setFirstLaunch(false)

                delay(200)

                println("🟢 [Splash] Loaded first URL → $fetchedUrl")
                _uiState.value = SplashUiState.ShowWeb(fetchedUrl)
                return
            }

            println("🔴 [Splash] invalid URL → ShowApp")
            _uiState.value = SplashUiState.ShowApp
            return
        }

        println("🟧 [Splash] Step 6: not first launch → ShowApp")
        _uiState.value = SplashUiState.ShowApp
    }

    fun splashDelay(seconds: Long = 1L): Flow<Unit> = flow {
        println("🕒 [Splash] splashDelay: waiting $seconds seconds…")
        delay(seconds * 1000)
        println("🕒 [Splash] splashDelay: done")
        emit(Unit)
    }

    suspend fun isFirstLaunch(): Boolean {
        val res = settings.isFirstLaunch()
        println("🟣 [Splash] isFirstLaunch() → $res")
        return res
    }

    suspend fun markLaunched() {
        println("🟡 [Splash] markLaunched()")
        settings.setFirstLaunch(false)
    }
}

sealed class SplashUiState {
    object Loading : SplashUiState()
    object ShowApp : SplashUiState()
    data class ShowWeb(val url: String) : SplashUiState()
}