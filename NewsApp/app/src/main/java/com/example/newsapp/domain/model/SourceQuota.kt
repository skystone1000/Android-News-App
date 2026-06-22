package com.example.newsapp.domain.model

/** Reset window for a provider's free-tier quota. */
enum class QuotaPeriod { DAILY, MONTHLY }

/**
 * A provider's free-tier request allowance, used to drive the in-app usage meter.
 * These are approximate and must be verified against each provider's current docs.
 */
data class SourceQuota(
    val limit: Int,
    val period: QuotaPeriod
)
