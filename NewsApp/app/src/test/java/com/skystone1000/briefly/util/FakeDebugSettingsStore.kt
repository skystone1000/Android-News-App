package com.skystone1000.briefly.util

import com.skystone1000.briefly.domain.debug.DebugConfig
import com.skystone1000.briefly.domain.debug.DebugSettingsStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** In-memory [DebugSettingsStore] for tests. */
class FakeDebugSettingsStore : DebugSettingsStore {

    val state = MutableStateFlow(DebugConfig())

    override fun config(): Flow<DebugConfig> = state

    override suspend fun setSaveResponses(enabled: Boolean) {
        state.value = state.value.copy(saveResponses = enabled)
    }

    override suspend fun setOfflineMode(enabled: Boolean) {
        state.value = state.value.copy(offlineMode = enabled)
    }

    override suspend fun setForceError(enabled: Boolean) {
        state.value = state.value.copy(forceError = enabled)
    }

    override suspend fun setLatencyMs(latencyMs: Long) {
        state.value = state.value.copy(latencyMs = latencyMs)
    }

    override suspend fun clear() {
        state.value = DebugConfig()
    }
}
