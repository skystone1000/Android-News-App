package com.skystone1000.briefly.data.remote.source

import com.skystone1000.briefly.data.remote.api.GNewsService
import com.skystone1000.briefly.data.remote.dto.toArticleOrNull
import com.skystone1000.briefly.domain.security.ApiKeyStore

/**
 * gnews.io source. Numeric `page` paging, `max` page size. The GNews **free plan caps `max` at 10**
 * per request and rejects larger values, so the requested page size is clamped to [FREE_MAX_RESULTS].
 */
class GNewsSource(
    private val service: GNewsService,
    private val apiKeyStore: ApiKeyStore
) : NewsSource {

    override val id: String = ID

    override suspend fun getNews(category: String?, cursor: String?, pageSize: Int): NewsPage {
        val page = pageOf(cursor)
        val articles = service.getTopHeadlines(
            category = category,
            page = page,
            max = pageSize.coerceAtMost(FREE_MAX_RESULTS),
            apiKey = apiKey()
        ).articles.orEmpty().mapNotNull { it.toArticleOrNull() }
        return NewsPage(articles, nextPageCursor(page, articles))
    }

    override suspend fun searchNews(query: String, cursor: String?, pageSize: Int): NewsPage {
        val page = pageOf(cursor)
        val articles = service.searchNews(
            query = query,
            page = page,
            max = pageSize.coerceAtMost(FREE_MAX_RESULTS),
            apiKey = apiKey()
        ).articles.orEmpty().mapNotNull { it.toArticleOrNull() }
        return NewsPage(articles, nextPageCursor(page, articles))
    }

    private suspend fun apiKey(): String =
        apiKeyStore.getKey(ID).ifBlank { throw MissingApiKeyException(ID) }

    companion object {
        const val ID = "gnews"

        /** Max articles GNews returns per request on the free plan; larger `max` values are rejected. */
        private const val FREE_MAX_RESULTS = 10
    }
}
