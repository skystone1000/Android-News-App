package com.skystone1000.briefly.domain.debug

import kotlinx.coroutines.flow.Flow

/** Persists [DebugConfig] (debug builds only). */
interface DebugSettingsStore {

    fun config(): Flow<DebugConfig>

    suspend fun setSaveResponses(enabled: Boolean)

    suspend fun setOfflineMode(enabled: Boolean)

    suspend fun setForceError(enabled: Boolean)

    suspend fun setLatencyMs(latencyMs: Long)

    /** Resets all debug flags to their defaults. */
    suspend fun clear()
}
