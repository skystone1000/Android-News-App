package com.skystone1000.briefly.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skystone1000.briefly.domain.manager.SettingsManager
import com.skystone1000.briefly.domain.model.ThemeMode
import com.skystone1000.briefly.domain.usecases.app_entry.AppEntryUseCases
import com.skystone1000.briefly.presentation.navgraph.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * Decides the start destination (onboarding vs. main) from the app-entry flag and
 * controls how long the splash screen stays up.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    appEntryUseCases: AppEntryUseCases,
    settingsManager: SettingsManager
) : ViewModel() {

    var splashCondition by mutableStateOf(true)
        private set

    var startDestination by mutableStateOf(Route.AppStartNavigation.route)
        private set

    var themeMode by mutableStateOf(ThemeMode.SYSTEM)
        private set

    init {
        appEntryUseCases.readAppEntry().onEach { shouldStartFromHomeScreen ->
            startDestination = if (shouldStartFromHomeScreen) {
                Route.NewsNavigation.route
            } else {
                Route.AppStartNavigation.route
            }
            // Brief hold so the start destination resolves before the splash hides.
            delay(SPLASH_DELAY_MS)
            splashCondition = false
        }.launchIn(viewModelScope)

        settingsManager.settings().onEach { themeMode = it.themeMode }.launchIn(viewModelScope)
    }

    private companion object {
        const val SPLASH_DELAY_MS = 200L
    }
}
