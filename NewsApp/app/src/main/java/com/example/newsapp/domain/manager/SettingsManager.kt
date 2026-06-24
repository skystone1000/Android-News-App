package com.example.newsapp.domain.manager

import com.example.newsapp.domain.model.ThemeMode
import com.example.newsapp.domain.model.UserSettings
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
