package com.example.newsapp.presentation.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.domain.debug.DebugConfig
import com.example.newsapp.domain.debug.DebugSettingsStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DebugViewModel @Inject constructor(
    private val store: DebugSettingsStore,
) : ViewModel() {

    val config: StateFlow<DebugConfig> = store.config().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = DebugConfig(),
    )

    fun setSaveResponses(enabled: Boolean) {
        viewModelScope.launch { store.setSaveResponses(enabled) }
    }

    fun setOfflineMode(enabled: Boolean) {
        viewModelScope.launch { store.setOfflineMode(enabled) }
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5000L
    }
}
