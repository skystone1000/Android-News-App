package com.skystone1000.briefly.data.debug

import com.skystone1000.briefly.data.local.NewsDatabase
import com.skystone1000.briefly.domain.manager.SettingsManager
import com.skystone1000.briefly.domain.model.SourceCatalog
import com.skystone1000.briefly.domain.security.ApiKeyStore
import com.skystone1000.briefly.domain.usage.ApiUsageStore
import com.skystone1000.briefly.domain.debug.DebugSettingsStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/** Destructive debug helpers (debug builds only). */
@Singleton
class DebugActions @Inject constructor(
    private val mockStore: MockStore,
    private val database: NewsDatabase,
    private val apiKeyStore: ApiKeyStore,
    private val settingsManager: SettingsManager,
    private val usageStore: ApiUsageStore,
    private val debugStore: DebugSettingsStore,
) {
    /** Deletes all captured mock responses from the device. */
    suspend fun clearMocks() = withContext(Dispatchers.IO) {
        mockStore.clearAll()
    }

    /** Wipes Room tables, API keys, user/usage/debug settings — a clean-slate reset. */
    suspend fun resetAppState() = withContext(Dispatchers.IO) {
        database.clearAllTables()
        SourceCatalog.ALL_SOURCES.forEach { apiKeyStore.clearKey(it.id) }
        settingsManager.clear()
        usageStore.clearAll()
        debugStore.clear()
    }
}
