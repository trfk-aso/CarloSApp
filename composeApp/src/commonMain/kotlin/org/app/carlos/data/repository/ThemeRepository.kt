package org.app.carlos.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.app.carlos.data.CarloSApp
import org.app.carlos.data.model.Theme

interface ThemeRepository {
    suspend fun initializeThemes()
    suspend fun getAllThemes(): List<Theme>
    suspend fun setCurrentTheme(themeId: String)
    suspend fun getCurrentThemeId(): String?
    suspend fun markThemePurchased(themeId: String)

    val currentThemeId: StateFlow<String?>
}

class ThemeRepositoryImpl(
    private val db: CarloSApp
) : ThemeRepository {

    private val queries = db.carlosQueries

    private val _currentThemeId = MutableStateFlow<String?>(null)
    override val currentThemeId: StateFlow<String?> = _currentThemeId.asStateFlow()

    override suspend fun initializeThemes() {
        val existing = queries.getThemes().executeAsList()
        if (existing.isEmpty()) {
            queries.insertTheme(
                id = "default",
                name = "Default",
                isPurchased = 1,
                type = "free",
                previewRes = "bg_default",
                primaryColor = 0xFFFFFFFF,
                splashText = "Track easy!",
                price = null
            )
            queries.insertTheme(
                id = "midnight",
                name = "Midnight",
                isPurchased = 0,
                type = "paid",
                previewRes = "bg_midnight",
                primaryColor = 0xFF000000,
                splashText = "Dark mode vibes!",
                price = 1.99
            )
            queries.insertTheme(
                id = "solaris",
                name = "Solaris",
                isPurchased = 0,
                type = "paid",
                previewRes = "bg_solaris",
                primaryColor = 0xFFFFD700,
                splashText = "Shine on!",
                price = 2.99
            )
            queries.insertTheme(
                id = "marine",
                name = "Marine",
                isPurchased = 0,
                type = "paid",
                previewRes = "bg_marine",
                primaryColor = 0xFF4285F5,
                splashText = "Fresh and cool!",
                price = 1.49
            )
        }

        if (_currentThemeId.value == null) {
            _currentThemeId.value = queries.getCurrentThemeId().executeAsOneOrNull()
                ?: "default"
        }
    }

    override suspend fun getAllThemes(): List<Theme> {
        return queries.getThemes().executeAsList().map {
            Theme(
                id = it.id,
                name = it.name,
                isPurchased = it.isPurchased == 1L,
                type = it.type!!,
                previewRes = it.previewRes ?: "",
                primaryColor = it.primaryColor ?: 0L,
                splashText = it.splashText ?: "",
                price = it.price
            )
        }
    }

    override suspend fun setCurrentTheme(themeId: String) {
        queries.insertCurrentTheme(themeId)
        _currentThemeId.value = themeId
    }

    override suspend fun getCurrentThemeId(): String? {
        return _currentThemeId.value
    }

    override suspend fun markThemePurchased(themeId: String) {
        queries.purchaseTheme(themeId)
    }
}
