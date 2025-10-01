package org.app.carlos.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.app.carlos.data.repository.ExpenseRepository
import org.app.carlos.data.repository.SettingsRepository

data class ThemeUiState(
    val id: String,
    val name: String,
    val price: String = "",
    val isSelected: Boolean = false,
    val isPurchased: Boolean = false,
    val isLoading: Boolean = false,
    val hasError: Boolean = false
)

data class SettingsUiState(
    val themes: List<ThemeUiState> = emptyList(),
    val fuelUnit: String = "Liters",
    val showResetDialog: Boolean = false,
    val showUnlockDialog: String? = null
)

class SettingsViewModel(
    private val repository: ExpenseRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadThemes()
        loadPreferences()
    }

    private fun loadThemes() {
        // Заглушка, в реале можно подгружать из магазина или локально
        val themes = listOf(
            ThemeUiState(id = "default", name = "Default", isSelected = true, isPurchased = true),
            ThemeUiState(id = "midnight", name = "Midnight", price = "$1.99"),
            ThemeUiState(id = "solaris", name = "Solaris", price = "$2.99"),
            ThemeUiState(id = "marine", name = "Marine", price = "$1.49")
        )
        _uiState.value = _uiState.value.copy(themes = themes)
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            val unit = settingsRepository.getFuelUnit()
            _uiState.update { it.copy(fuelUnit = unit) }
        }
    }

    fun setFuelUnit(unit: String) {
        _uiState.update { it.copy(fuelUnit = unit) }
        viewModelScope.launch {
            settingsRepository.setFuelUnit(unit)
        }
    }

    fun showResetDialog() {
        _uiState.value = _uiState.value.copy(showResetDialog = true)
    }

    fun hideResetDialog() {
        _uiState.value = _uiState.value.copy(showResetDialog = false)
    }

    fun resetAllData() {
        viewModelScope.launch {
            // Удаляем все расходы
            repository.deleteAll()
            // Очищаем последние поисковые запросы
            settingsRepository.clearRecentSearches()
            // Закрываем диалог
            hideResetDialog()
        }
    }

    fun buyTheme(themeId: String) {
        // TODO: подключить покупку
        val updatedThemes = _uiState.value.themes.map {
            if (it.id == themeId) it.copy(isLoading = true) else it
        }
        _uiState.value = _uiState.value.copy(themes = updatedThemes)

        // Заглушка: через 2 сек считаем, что покупка прошла
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000)
            val newThemes = _uiState.value.themes.map {
                when {
                    it.id == themeId -> it.copy(isPurchased = true, isLoading = false)
                    it.isSelected -> it.copy(isSelected = false)
                    else -> it
                }
            }
            _uiState.value = _uiState.value.copy(
                themes = newThemes.map { if (it.id == themeId) it.copy(isSelected = true) else it },
                showUnlockDialog = null
            )
        }
    }

    fun useTheme(themeId: String) {
        val updatedThemes = _uiState.value.themes.map {
            it.copy(isSelected = it.id == themeId)
        }
        _uiState.value = _uiState.value.copy(themes = updatedThemes)
    }

    fun restorePurchases() {
        // TODO: интеграция с магазином
        // Пока просто делаем все темы купленными
        val updatedThemes = _uiState.value.themes.map { it.copy(isPurchased = true) }
        _uiState.value = _uiState.value.copy(themes = updatedThemes)
    }

    fun showUnlockDialog(themeId: String) {
        _uiState.value = _uiState.value.copy(showUnlockDialog = themeId)
    }

    fun dismissUnlockDialog() {
        _uiState.value = _uiState.value.copy(showUnlockDialog = null)
    }
}
