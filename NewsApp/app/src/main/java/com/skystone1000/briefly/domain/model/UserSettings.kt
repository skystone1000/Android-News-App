package com.skystone1000.briefly.domain.model

enum class ThemeMode { LIGHT, DARK, SYSTEM }

/** Categories supported across providers (intersection of NewsAPI + GNews). */
object NewsCategories {
    const val GENERAL = "general"
    val ALL = listOf(
        "general", "business", "technology", "entertainment", "sports", "science", "health"
    )
}

/** Default source id (kept as a literal so the domain stays free of data-layer types). */
const val DEFAULT_SOURCE_ID = "newsapi"

/**
 * User-configurable preferences. Defaults reproduce a plain generic reader, so every
 * feature is opt-in.
 */
data class UserSettings(
    val dataSourceId: String = DEFAULT_SOURCE_ID,
    val selectedCategory: String = NewsCategories.GENERAL,
    val followedCategories: Set<String> = emptySet(),
    val personalizationEnabled: Boolean = false,
    val aiSummariesEnabled: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)
