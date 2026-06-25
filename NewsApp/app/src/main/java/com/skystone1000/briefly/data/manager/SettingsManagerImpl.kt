package com.skystone1000.briefly.data.manager

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.skystone1000.briefly.domain.manager.SettingsManager
import com.skystone1000.briefly.domain.model.DEFAULT_SOURCE_ID
import com.skystone1000.briefly.domain.model.NewsCategories
import com.skystone1000.briefly.domain.model.ThemeMode
import com.skystone1000.briefly.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsManagerImpl(
    private val context: Context
) : SettingsManager {

    override fun settings(): Flow<UserSettings> = context.settingsDataStore.data.map { prefs ->
        UserSettings(
            dataSourceId = prefs[Keys.DATA_SOURCE] ?: DEFAULT_SOURCE_ID,
            selectedCategory = prefs[Keys.CATEGORY] ?: NewsCategories.GENERAL,
            followedCategories = prefs[Keys.FOLLOWED] ?: emptySet(),
            personalizationEnabled = prefs[Keys.PERSONALIZATION] ?: false,
            aiSummariesEnabled = prefs[Keys.AI_SUMMARIES] ?: false,
            themeMode = prefs[Keys.THEME]?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
                ?: ThemeMode.SYSTEM
        )
    }

    override suspend fun setDataSource(id: String) {
        context.settingsDataStore.edit { it[Keys.DATA_SOURCE] = id }
    }

    override suspend fun setSelectedCategory(category: String) {
        context.settingsDataStore.edit { it[Keys.CATEGORY] = category }
    }

    override suspend fun setFollowedCategories(categories: Set<String>) {
        context.settingsDataStore.edit { it[Keys.FOLLOWED] = categories }
    }

    override suspend fun setPersonalizationEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.PERSONALIZATION] = enabled }
    }

    override suspend fun setAiSummariesEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.AI_SUMMARIES] = enabled }
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        context.settingsDataStore.edit { it[Keys.THEME] = mode.name }
    }

    override suspend fun clear() {
        context.settingsDataStore.edit { it.clear() }
    }

    private object Keys {
        val DATA_SOURCE = stringPreferencesKey("data_source")
        val CATEGORY = stringPreferencesKey("selected_category")
        val FOLLOWED = stringSetPreferencesKey("followed_categories")
        val PERSONALIZATION = booleanPreferencesKey("personalization_enabled")
        val AI_SUMMARIES = booleanPreferencesKey("ai_summaries_enabled")
        val THEME = stringPreferencesKey("theme_mode")
    }
}

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "news_user_settings"
)
