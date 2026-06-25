package com.skystone1000.briefly.presentation.settings

import com.skystone1000.briefly.domain.model.ThemeMode

sealed class SettingsEvent {
    data class SetDataSource(val id: String) : SettingsEvent()
    data class SetApiKey(val sourceId: String, val key: String) : SettingsEvent()
    data class ClearApiKey(val sourceId: String) : SettingsEvent()
    data class SetThemeMode(val mode: ThemeMode) : SettingsEvent()
    data class ToggleFollowedCategory(val category: String) : SettingsEvent()
    data class SetPersonalization(val enabled: Boolean) : SettingsEvent()
    data class SetAiSummaries(val enabled: Boolean) : SettingsEvent()
}
