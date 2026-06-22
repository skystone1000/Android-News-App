package com.example.newsapp.domain.model

/**
 * Static description of a selectable news provider. UI-facing metadata (display name, where to
 * get a key) plus the free-tier [quota] that drives the usage meter. Concrete `NewsSource`
 * implementations are registered separately via Hilt; this is the catalog the Settings UI renders.
 */
data class SourceMetadata(
    val id: String,
    val displayName: String,
    val signupUrl: String,
    val keyParamHint: String,
    val quota: SourceQuota
)

/** All providers the app knows about. Verify quotas/URLs against current provider docs. */
object SourceCatalog {

    val ALL_SOURCES: List<SourceMetadata> = listOf(
        SourceMetadata(
            id = "newsapi",
            displayName = "NewsAPI.org",
            signupUrl = "https://newsapi.org/register",
            keyParamHint = "apiKey",
            quota = SourceQuota(limit = 100, period = QuotaPeriod.DAILY)
        ),
        SourceMetadata(
            id = "newsdata",
            displayName = "NewsData.io",
            signupUrl = "https://newsdata.io/register",
            keyParamHint = "apikey",
            quota = SourceQuota(limit = 200, period = QuotaPeriod.DAILY)
        ),
        SourceMetadata(
            id = "gnews",
            displayName = "GNews",
            signupUrl = "https://gnews.io/register",
            keyParamHint = "apikey",
            quota = SourceQuota(limit = 100, period = QuotaPeriod.DAILY)
        ),
        SourceMetadata(
            id = "currents",
            displayName = "Currents",
            signupUrl = "https://currentsapi.services/en/register",
            keyParamHint = "apiKey",
            quota = SourceQuota(limit = 600, period = QuotaPeriod.DAILY)
        ),
        SourceMetadata(
            id = "mediastack",
            displayName = "Mediastack",
            signupUrl = "https://mediastack.com/signup/free",
            keyParamHint = "access_key",
            quota = SourceQuota(limit = 500, period = QuotaPeriod.MONTHLY)
        )
    )

    fun byId(id: String): SourceMetadata? = ALL_SOURCES.firstOrNull { it.id == id }
}
