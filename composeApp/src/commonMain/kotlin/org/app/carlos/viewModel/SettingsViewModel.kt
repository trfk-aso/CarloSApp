package org.app.carlos.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.app.carlos.data.repository.BillingRepository
import org.app.carlos.data.repository.ExpenseRepository
import org.app.carlos.data.repository.PurchaseResult
import org.app.carlos.data.repository.SettingsRepository
import org.app.carlos.data.repository.ThemeRepository

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
    private val settingsRepository: SettingsRepository,
    private val themeRepository: ThemeRepository,
    private val billingRepository: BillingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadThemes()
        loadPreferences()
    }

    private fun loadThemes() {
        viewModelScope.launch {
            val themesFromDb = themeRepository.getAllThemes()
            val currentThemeId = themeRepository.getCurrentThemeId()

            val themesUi = themesFromDb.map { theme ->
                ThemeUiState(
                    id = theme.id,
                    name = theme.name,
                    price = theme.price?.let { "$$it" } ?: "",
                    isSelected = theme.id == currentThemeId,
                    isPurchased = theme.isPurchased
                )
            }

            _uiState.value = _uiState.value.copy(themes = themesUi)
        }
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
            repository.deleteAll()
            settingsRepository.clearRecentSearches()
            hideResetDialog()
        }
    }

    fun buyTheme(themeId: String) {
        val updatedThemes = _uiState.value.themes.map {
            if (it.id == themeId) it.copy(isLoading = true) else it
        }
        _uiState.value = _uiState.value.copy(themes = updatedThemes)

        viewModelScope.launch {
            val result = billingRepository.purchaseTheme(themeId)
            when (result) {
                is PurchaseResult.Success -> {
                    themeRepository.markThemePurchased(themeId)
                    themeRepository.setCurrentTheme(themeId)
                    _uiState.update { it.copy(showUnlockDialog = null) }
                    loadThemes()
                }
                is PurchaseResult.Failure -> {
                    _uiState.value = _uiState.value.copy(
                        themes = _uiState.value.themes.map {
                            if (it.id == themeId) it.copy(isLoading = false, hasError = true) else it
                        }
                    )
                }
                is PurchaseResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        themes = _uiState.value.themes.map {
                            if (it.id == themeId) it.copy(isLoading = false, hasError = true) else it
                        }
                    )
                }
            }
        }
    }

    fun useTheme(themeId: String) {
        viewModelScope.launch {
            themeRepository.setCurrentTheme(themeId)
            loadThemes()
        }
    }

    fun restorePurchases() {
        viewModelScope.launch {
            val result = billingRepository.restorePurchases()
            if (result is PurchaseResult.Success) {
                loadThemes()
            }
        }
    }

    fun showUnlockDialog(themeId: String) {
        _uiState.value = _uiState.value.copy(showUnlockDialog = themeId)
    }

    fun dismissUnlockDialog() {
        _uiState.value = _uiState.value.copy(showUnlockDialog = null)
    }
}

//data class ThemeUiState(
//    val id: String,
//    val name: String,
//    val price: String = "",
//    val isSelected: Boolean = false,
//    val isPurchased: Boolean = false,
//    val isLoading: Boolean = false,
//    val hasError: Boolean = false
//)
//
//data class SettingsUiState(
//    val themes: List<ThemeUiState> = emptyList(),
//    val fuelUnit: String = "Liters",
//    val showResetDialog: Boolean = false,
//    val showUnlockDialog: String? = null
//)
//
//class SettingsViewModel(
//    private val repository: ExpenseRepository,
//    private val settingsRepository: SettingsRepository,
//    private val themeRepository: ThemeRepository
//) : ViewModel() {
//
//    private val _uiState = MutableStateFlow(SettingsUiState())
//    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
//
//    init {
//        loadThemes()
//        loadPreferences()
//    }
//
//    private fun loadThemes() {
//        viewModelScope.launch {
//            val themesFromDb = themeRepository.getAllThemes()
//            val currentThemeId = themeRepository.getCurrentThemeId()
//
//            val themesUi = themesFromDb.map { theme ->
//                ThemeUiState(
//                    id = theme.id,
//                    name = theme.name,
//                    price = theme.price?.let { "$$it" } ?: "",
//                    isSelected = theme.id == currentThemeId,
//                    isPurchased = theme.isPurchased
//                )
//            }
//
//            _uiState.value = _uiState.value.copy(themes = themesUi)
//        }
//    }
//
//    private fun loadPreferences() {
//        viewModelScope.launch {
//            val unit = settingsRepository.getFuelUnit()
//            _uiState.update { it.copy(fuelUnit = unit) }
//        }
//    }
//
//    fun setFuelUnit(unit: String) {
//        _uiState.update { it.copy(fuelUnit = unit) }
//        viewModelScope.launch {
//            settingsRepository.setFuelUnit(unit)
//        }
//    }
//
//    fun showResetDialog() {
//        _uiState.value = _uiState.value.copy(showResetDialog = true)
//    }
//
//    fun hideResetDialog() {
//        _uiState.value = _uiState.value.copy(showResetDialog = false)
//    }
//
//    fun resetAllData() {
//        viewModelScope.launch {
//            repository.deleteAll()
//            settingsRepository.clearRecentSearches()
//            hideResetDialog()
//        }
//    }
//
//    fun buyTheme(themeId: String) {
//        _uiState.update { state ->
//            val updatedThemes = state.themes.map {
//                if (it.id == themeId) it.copy(isPurchased = true, isSelected = true, isLoading = false, hasError = false)
//                else it.copy(isSelected = false)
//            }
//            state.copy(
//                themes = updatedThemes,
//                showUnlockDialog = null
//            )
//        }
//
//        viewModelScope.launch {
//            themeRepository.markThemePurchased(themeId)
//            themeRepository.setCurrentTheme(themeId)
//        }
//    }
//
//    fun useTheme(themeId: String) {
//        _uiState.update { state ->
//            val updatedThemes = state.themes.map {
//                it.copy(isSelected = it.id == themeId)
//            }
//            state.copy(themes = updatedThemes)
//        }
//
//        viewModelScope.launch {
//            themeRepository.setCurrentTheme(themeId)
//        }
//    }
//
//    fun restorePurchases() {
//        _uiState.update { state ->
//            val updatedThemes = state.themes.map {
//                it.copy(isPurchased = true)
//            }
//            state.copy(themes = updatedThemes)
//        }
//    }
//
//    fun showUnlockDialog(themeId: String) {
//        _uiState.value = _uiState.value.copy(showUnlockDialog = themeId)
//    }
//
//    fun dismissUnlockDialog() {
//        _uiState.value = _uiState.value.copy(showUnlockDialog = null)
//    }
//}
//
