package com.skystone1000.briefly.data.debug

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.skystone1000.briefly.domain.debug.DebugConfig
import com.skystone1000.briefly.domain.debug.DebugSettingsStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DebugSettingsStoreImpl(
    private val context: Context
) : DebugSettingsStore {

    override fun config(): Flow<DebugConfig> = context.debugDataStore.data.map { prefs ->
        DebugConfig(
            saveResponses = prefs[Keys.SAVE_RESPONSES] ?: false,
            offlineMode = prefs[Keys.OFFLINE_MODE] ?: false,
            forceError = prefs[Keys.FORCE_ERROR] ?: false,
            latencyMs = prefs[Keys.LATENCY_MS] ?: 0L,
        )
    }

    override suspend fun setSaveResponses(enabled: Boolean) {
        context.debugDataStore.edit { it[Keys.SAVE_RESPONSES] = enabled }
    }

    override suspend fun setOfflineMode(enabled: Boolean) {
        context.debugDataStore.edit { it[Keys.OFFLINE_MODE] = enabled }
    }

    override suspend fun setForceError(enabled: Boolean) {
        context.debugDataStore.edit { it[Keys.FORCE_ERROR] = enabled }
    }

    override suspend fun setLatencyMs(latencyMs: Long) {
        context.debugDataStore.edit { it[Keys.LATENCY_MS] = latencyMs }
    }

    override suspend fun clear() {
        context.debugDataStore.edit { it.clear() }
    }

    private object Keys {
        val SAVE_RESPONSES = booleanPreferencesKey("save_responses")
        val OFFLINE_MODE = booleanPreferencesKey("offline_mode")
        val FORCE_ERROR = booleanPreferencesKey("force_error")
        val LATENCY_MS = longPreferencesKey("latency_ms")
    }
}

private val Context.debugDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "debug_settings"
)
