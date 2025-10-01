package org.app.carlos.data.repository

import com.russhwolf.settings.Settings

interface SettingsRepository {
    suspend fun isFirstLaunch(): Boolean
    suspend fun setFirstLaunch(value: Boolean)

    suspend fun getRecentSearches(): List<String>
    suspend fun addRecentSearch(query: String)
    suspend fun clearRecentSearches()

    suspend fun getFuelUnit(): String
    suspend fun setFuelUnit(value: String)
}

class SettingsRepositoryImpl(
    private val settings: Settings
) : SettingsRepository {

    override suspend fun isFirstLaunch(): Boolean {
        return settings.getBoolean("first_launch", true)
    }

    override suspend fun setFirstLaunch(value: Boolean) {
        settings.putBoolean("first_launch", value)
    }

    override suspend fun getRecentSearches(): List<String> {
        val serialized = settings.getString("recent_searches", "")
        if (serialized.isBlank()) return emptyList()
        return serialized.split("||").filter { it.isNotBlank() }
    }

    override suspend fun addRecentSearch(query: String) {
        val current = getRecentSearches().toMutableList()
        current.remove(query)
        current.add(0, query)
        val limited = current.take(5)
        val serialized = limited.joinToString("||")
        settings.putString("recent_searches", serialized)
    }

    override suspend fun clearRecentSearches() {
        settings.putString("recent_searches", "")
    }

    override suspend fun getFuelUnit(): String {
        return settings.getString("fuel_unit", "Liters")
    }

    override suspend fun setFuelUnit(value: String) {
        settings.putString("fuel_unit", value)
    }
}