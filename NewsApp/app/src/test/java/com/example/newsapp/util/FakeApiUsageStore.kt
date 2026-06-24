package com.example.newsapp.util

import com.example.newsapp.domain.model.SourceCatalog
import com.example.newsapp.domain.usage.ApiUsageStore
import com.example.newsapp.domain.usage.QuotaWindow
import com.example.newsapp.domain.usage.UsageSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** In-memory [ApiUsageStore] for tests (no DataStore / Android deps). */
class FakeApiUsageStore(
    initialUsed: Map<String, Int> = emptyMap()
) : ApiUsageStore {

    private val counts = HashMap(initialUsed)
    private val flow = MutableStateFlow(snapshots())

    override fun usage(): Flow<Map<String, UsageSnapshot>> = flow

    override suspend fun recordRequest(sourceId: String) {
        counts[sourceId] = (counts[sourceId] ?: 0) + 1
        flow.value = snapshots()
    }

    override suspend fun recordRemaining(sourceId: String, remaining: Int) {
        val limit = SourceCatalog.byId(sourceId)?.quota?.limit ?: return
        counts[sourceId] = (limit - remaining).coerceIn(0, limit)
        flow.value = snapshots()
    }

    override suspend fun reset(sourceId: String) {
        counts[sourceId] = 0
        flow.value = snapshots()
    }

    override suspend fun clearAll() {
        counts.clear()
        flow.value = snapshots()
    }

    private fun snapshots(): Map<String, UsageSnapshot> {
        val now = 0L
        return SourceCatalog.ALL_SOURCES.associate { source ->
            source.id to UsageSnapshot(
                used = counts[source.id] ?: 0,
                limit = source.quota.limit,
                period = source.quota.period,
                windowStart = QuotaWindow.windowStart(source.quota.period, now),
                resetAt = QuotaWindow.resetAt(source.quota.period, now)
            )
        }
    }
}
