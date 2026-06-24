package com.example.newsapp.util

import com.example.newsapp.domain.manager.SettingsManager
import com.example.newsapp.domain.model.ThemeMode
import com.example.newsapp.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** In-memory [SettingsManager] for ViewModel tests. */
class FakeSettingsManager : SettingsManager {

    val state = MutableStateFlow(UserSettings())

    override fun settings(): Flow<UserSettings> = state

    override suspend fun setDataSource(id: String) {
        state.value = state.value.copy(dataSourceId = id)
    }

    override suspend fun setSelectedCategory(category: String) {
        state.value = state.value.copy(selectedCategory = category)
    }

    override suspend fun setFollowedCategories(categories: Set<String>) {
        state.value = state.value.copy(followedCategories = categories)
    }

    override suspend fun setPersonalizationEnabled(enabled: Boolean) {
        state.value = state.value.copy(personalizationEnabled = enabled)
    }

    override suspend fun setAiSummariesEnabled(enabled: Boolean) {
        state.value = state.value.copy(aiSummariesEnabled = enabled)
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        state.value = state.value.copy(themeMode = mode)
    }

    override suspend fun clear() {
        state.value = UserSettings()
    }
}
