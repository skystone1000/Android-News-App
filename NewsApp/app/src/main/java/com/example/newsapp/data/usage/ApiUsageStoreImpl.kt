package com.example.newsapp.data.usage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.newsapp.domain.model.SourceCatalog
import com.example.newsapp.domain.model.SourceMetadata
import com.example.newsapp.domain.usage.ApiUsageStore
import com.example.newsapp.domain.usage.QuotaWindow
import com.example.newsapp.domain.usage.UsageSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * [ApiUsageStore] persisted in a dedicated Preferences DataStore. Each provider stores a `count`
 * and the `windowStart` it belongs to; reads/writes roll the window over when the local day/month
 * boundary is crossed, so a stale window reads as zero used.
 */
class ApiUsageStoreImpl(
    private val context: Context,
    private val now: () -> Long = System::currentTimeMillis
) : ApiUsageStore {

    override fun usage(): Flow<Map<String, UsageSnapshot>> =
        context.usageDataStore.data.map { prefs ->
            val current = now()
            SourceCatalog.ALL_SOURCES.associate { source ->
                source.id to snapshot(source, prefs, current)
            }
        }

    override suspend fun recordRequest(sourceId: String) {
        val source = SourceCatalog.byId(sourceId) ?: return
        context.usageDataStore.edit { prefs ->
            val windowStart = QuotaWindow.windowStart(source.quota.period, now())
            val sameWindow = prefs[windowKey(sourceId)] == windowStart
            prefs[countKey(sourceId)] = if (sameWindow) (prefs[countKey(sourceId)] ?: 0) + 1 else 1
            prefs[windowKey(sourceId)] = windowStart
        }
    }

    override suspend fun recordRemaining(sourceId: String, remaining: Int) {
        val source = SourceCatalog.byId(sourceId) ?: return
        context.usageDataStore.edit { prefs ->
            prefs[windowKey(sourceId)] = QuotaWindow.windowStart(source.quota.period, now())
            prefs[countKey(sourceId)] = (source.quota.limit - remaining).coerceIn(0, source.quota.limit)
        }
    }

    override suspend fun reset(sourceId: String) {
        val source = SourceCatalog.byId(sourceId) ?: return
        context.usageDataStore.edit { prefs ->
            prefs[countKey(sourceId)] = 0
            prefs[windowKey(sourceId)] = QuotaWindow.windowStart(source.quota.period, now())
        }
    }

    private fun snapshot(source: SourceMetadata, prefs: Preferences, current: Long): UsageSnapshot {
        val windowStart = QuotaWindow.windowStart(source.quota.period, current)
        val storedWindow = prefs[windowKey(source.id)]
        val used = if (storedWindow == windowStart) prefs[countKey(source.id)] ?: 0 else 0
        return UsageSnapshot(
            used = used,
            limit = source.quota.limit,
            period = source.quota.period,
            windowStart = windowStart,
            resetAt = QuotaWindow.resetAt(source.quota.period, current)
        )
    }

    private fun countKey(id: String) = intPreferencesKey("count_$id")
    private fun windowKey(id: String) = longPreferencesKey("window_$id")
}

private val Context.usageDataStore: DataStore<Preferences> by preferencesDataStore(name = "api_usage")
