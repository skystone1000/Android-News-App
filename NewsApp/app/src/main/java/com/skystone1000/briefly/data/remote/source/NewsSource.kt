package com.skystone1000.briefly.data.remote.source

import com.skystone1000.briefly.domain.model.Article

/**
 * One page of articles plus an opaque [nextCursor] for the following page. [nextCursor] is `null`
 * when there are no more results. The cursor is provider-defined: numeric providers encode a page
 * number, offset providers encode an offset, and cursor providers pass their own token through.
 */
data class NewsPage(
    val articles: List<Article>,
    val nextCursor: String?
)

/**
 * Provider-agnostic access to news. Each backend (NewsAPI, GNews, …) implements this;
 * the rest of the app never depends on a concrete provider. Adding a provider = adding
 * one implementation + one Hilt map binding.
 *
 * Implementations read their API key at call time from
 * [com.skystone1000.briefly.domain.security.ApiKeyStore] and throw [MissingApiKeyException] when the
 * user has not configured one.
 */
interface NewsSource {

    /** Stable identifier used for runtime selection (e.g. "newsapi", "gnews"). */
    val id: String

    suspend fun getNews(category: String?, cursor: String?, pageSize: Int): NewsPage

    suspend fun searchNews(query: String, cursor: String?, pageSize: Int): NewsPage
}
