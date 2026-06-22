package com.example.newsapp.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.domain.manager.SettingsManager
import com.example.newsapp.domain.model.DEFAULT_SOURCE_ID
import com.example.newsapp.domain.model.UserSettings
import com.example.newsapp.domain.security.ApiKeyStore
import com.example.newsapp.domain.usage.ApiUsageStore
import com.example.newsapp.domain.usage.UsageSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val apiKeyStore: ApiKeyStore,
    private val apiUsageStore: ApiUsageStore
) : ViewModel() {

    val settings: StateFlow<UserSettings> = settingsManager.settings().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = UserSettings()
    )

    /** Source ids that currently have a key — drives gating of the active-source picker. */
    val configuredSourceIds: StateFlow<Set<String>> = apiKeyStore.keys().map { it.keys }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = emptySet()
    )

    val usage: StateFlow<Map<String, UsageSnapshot>> = apiUsageStore.usage().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = emptyMap()
    )

    fun onEvent(event: SettingsEvent) {
        viewModelScope.launch {
            when (event) {
                is SettingsEvent.SetDataSource -> settingsManager.setDataSource(event.id)
                is SettingsEvent.SetApiKey -> apiKeyStore.setKey(event.sourceId, event.key)
                is SettingsEvent.ClearApiKey -> clearKey(event.sourceId)
                is SettingsEvent.SetThemeMode -> settingsManager.setThemeMode(event.mode)
                is SettingsEvent.SetPersonalization ->
                    settingsManager.setPersonalizationEnabled(event.enabled)
                is SettingsEvent.SetAiSummaries ->
                    settingsManager.setAiSummariesEnabled(event.enabled)
                is SettingsEvent.ToggleFollowedCategory -> {
                    // Read-modify-write against the source of truth (not the derived UI
                    // StateFlow, which can lag) so rapid toggles stay correct.
                    val current = settingsManager.settings().first().followedCategories
                    val updated = current.toMutableSet()
                    if (!updated.add(event.category)) updated.remove(event.category)
                    settingsManager.setFollowedCategories(updated)
                }
            }
        }
    }

    /** Clears a key; if it was the active source, fall back to a still-configured source. */
    private suspend fun clearKey(sourceId: String) {
        apiKeyStore.clearKey(sourceId)
        if (settingsManager.settings().first().dataSourceId == sourceId) {
            val fallback = apiKeyStore.keys().first().keys.firstOrNull() ?: DEFAULT_SOURCE_ID
            settingsManager.setDataSource(fallback)
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5000L
    }
}
