package com.skystone1000.briefly.domain.manager

import com.skystone1000.briefly.domain.model.ThemeMode
import com.skystone1000.briefly.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

/** Reads and persists user-configurable settings. */
interface SettingsManager {

    fun settings(): Flow<UserSettings>

    suspend fun setDataSource(id: String)

    suspend fun setSelectedCategory(category: String)

    suspend fun setFollowedCategories(categories: Set<String>)

    suspend fun setPersonalizationEnabled(enabled: Boolean)

    suspend fun setAiSummariesEnabled(enabled: Boolean)

    suspend fun setThemeMode(mode: ThemeMode)

    /** Resets all user settings to their defaults (used by the debug reset action). */
    suspend fun clear()
}
