package com.example.newsapp.presentation.debug

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.debug.DebugActions
import com.example.newsapp.domain.debug.DebugConfig
import com.example.newsapp.domain.debug.DebugSettingsStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Latency presets the screen cycles through (ms). */
val LATENCY_PRESETS = listOf(0L, 500L, 1500L, 3000L)

@HiltViewModel
class DebugViewModel @Inject constructor(
    private val store: DebugSettingsStore,
    private val actions: DebugActions,
) : ViewModel() {

    val config: StateFlow<DebugConfig> = store.config().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = DebugConfig(),
    )

    /** One-shot confirmation message for a Toast. */
    var sideEffect by mutableStateOf<String?>(null)
        private set

    fun setSaveResponses(enabled: Boolean) = launchEdit { store.setSaveResponses(enabled) }
    fun setOfflineMode(enabled: Boolean) = launchEdit { store.setOfflineMode(enabled) }
    fun setForceError(enabled: Boolean) = launchEdit { store.setForceError(enabled) }

    /** Advances latency to the next preset and persists it. */
    fun cycleLatency() {
        val current = config.value.latencyMs
        val next = LATENCY_PRESETS[(LATENCY_PRESETS.indexOf(current).coerceAtLeast(0) + 1) % LATENCY_PRESETS.size]
        launchEdit { store.setLatencyMs(next) }
    }

    fun clearMocks() = viewModelScope.launch {
        actions.clearMocks()
        sideEffect = "Captured mocks cleared"
    }

    fun resetAppState() = viewModelScope.launch {
        actions.resetAppState()
        sideEffect = "App state reset"
    }

    fun consumeSideEffect() {
        sideEffect = null
    }

    private fun launchEdit(block: suspend () -> Unit) = viewModelScope.launch { block() }

    private companion object {
        const val STOP_TIMEOUT_MS = 5000L
    }
}
