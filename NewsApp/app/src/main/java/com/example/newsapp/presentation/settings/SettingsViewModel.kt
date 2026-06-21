package com.example.newsapp.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.domain.manager.SettingsManager
import com.example.newsapp.domain.model.UserSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : ViewModel() {

    val settings: StateFlow<UserSettings> = settingsManager.settings().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = UserSettings()
    )

    fun onEvent(event: SettingsEvent) {
        viewModelScope.launch {
            when (event) {
                is SettingsEvent.SetDataSource -> settingsManager.setDataSource(event.id)
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

    private companion object {
        const val STOP_TIMEOUT_MS = 5000L
    }
}
