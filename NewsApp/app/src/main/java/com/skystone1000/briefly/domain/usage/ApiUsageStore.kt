package com.skystone1000.briefly.domain.usage

import com.skystone1000.briefly.domain.model.QuotaPeriod
import kotlinx.coroutines.flow.Flow

/**
 * A point-in-time view of one provider's free-tier consumption for the current window.
 * [used] is an on-device estimate (see [ApiUsageStore]).
 */
data class UsageSnapshot(
    val used: Int,
    val limit: Int,
    val period: QuotaPeriod,
    val windowStart: Long,
    val resetAt: Long
)

/**
 * Tracks how many requests each provider has made in the current quota window so the UI can show
 * a usage meter. Counting is **on-device** — failed/cached requests or usage from other devices
 * won't be reflected, so it's an estimate. When a provider returns a rate-limit header, the
 * remaining value overrides the local count via [recordRemaining].
 */
interface ApiUsageStore {

    fun usage(): Flow<Map<String, UsageSnapshot>>

    /** Counts one request against [sourceId] in the current window (rolling it over if needed). */
    suspend fun recordRequest(sourceId: String)

    /** Reconciles the local count from a provider-reported `remaining` quota. */
    suspend fun recordRemaining(sourceId: String, remaining: Int)

    /** Clears the count for [sourceId] in the current window. */
    suspend fun reset(sourceId: String)

    /** Clears all usage counts (used by the debug reset action). */
    suspend fun clearAll()
}
