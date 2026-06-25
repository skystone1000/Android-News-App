package com.skystone1000.briefly.domain.ai

import com.skystone1000.briefly.domain.model.Article

/** AI-derived insight for an article. */
data class ArticleInsight(
    val summary: String,
    val sentiment: String,
    val tags: List<String>
)

/**
 * Provider-agnostic contract for AI enrichment. The implementation (Claude) and its
 * key handling live in the data layer; the rest of the app depends only on this.
 */
interface AiGateway {

    /** Summarize + tag + classify sentiment for [article]. Results are cached by URL. */
    suspend fun summarize(article: Article): Result<ArticleInsight>
}
