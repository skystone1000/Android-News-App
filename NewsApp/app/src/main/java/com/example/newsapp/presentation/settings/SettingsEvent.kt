package com.example.newsapp.presentation.settings

import com.example.newsapp.domain.model.ThemeMode

sealed class SettingsEvent {
    data class SetDataSource(val id: String) : SettingsEvent()
    data class SetThemeMode(val mode: ThemeMode) : SettingsEvent()
    data class ToggleFollowedCategory(val category: String) : SettingsEvent()
    data class SetPersonalization(val enabled: Boolean) : SettingsEvent()
    data class SetAiSummaries(val enabled: Boolean) : SettingsEvent()
}
