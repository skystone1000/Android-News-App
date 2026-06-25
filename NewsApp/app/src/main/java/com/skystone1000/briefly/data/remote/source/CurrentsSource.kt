package com.skystone1000.briefly.data.remote.source

import com.skystone1000.briefly.data.remote.api.CurrentsService
import com.skystone1000.briefly.data.remote.dto.toArticleOrNull
import com.skystone1000.briefly.domain.security.ApiKeyStore

/**
 * currentsapi.services source. The Currents API's `v1/latest-news` endpoint accepts only a
 * `language` filter and `v1/search` only `keywords`/`language` (see [CurrentsService]); neither
 * supports `page_number`/`page_size` or a category on latest-news, so both calls return a single,
 * non-paginated page ([NewsPage.nextCursor] is always `null`). Category selection therefore does
 * not narrow the Currents headlines feed.
 */
class CurrentsSource(
    private val service: CurrentsService,
    private val apiKeyStore: ApiKeyStore
) : NewsSource {

    override val id: String = ID

    override suspend fun getNews(category: String?, cursor: String?, pageSize: Int): NewsPage {
        val articles = service.getLatest(apiKey = apiKey())
            .news.orEmpty().mapNotNull { it.toArticleOrNull() }
        return NewsPage(articles, nextCursor = null)
    }

    override suspend fun searchNews(query: String, cursor: String?, pageSize: Int): NewsPage {
        val articles = service.searchNews(apiKey = apiKey(), keywords = query)
            .news.orEmpty().mapNotNull { it.toArticleOrNull() }
        return NewsPage(articles, nextCursor = null)
    }

    private suspend fun apiKey(): String =
        apiKeyStore.getKey(ID).ifBlank { throw MissingApiKeyException(ID) }

    companion object {
        const val ID = "currents"
    }
}
