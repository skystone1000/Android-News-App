package com.example.newsapp.domain.debug

/**
 * Debug-only runtime configuration (debug builds only). All flags default to off so a
 * fresh install behaves exactly like a normal build.
 *
 * - [saveResponses] tees every successful provider response to the on-device mock dir.
 * - [offlineMode] replays saved/bundled mocks instead of hitting the network.
 * - [forceError] makes provider calls fail (to exercise error/empty UI).
 * - [latencyMs] adds an artificial per-request delay (loading-state testing); 0 = off.
 */
data class DebugConfig(
    val saveResponses: Boolean = false,
    val offlineMode: Boolean = false,
    val forceError: Boolean = false,
    val latencyMs: Long = 0L,
)
