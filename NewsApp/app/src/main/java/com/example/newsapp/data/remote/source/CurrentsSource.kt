package com.example.newsapp.data.remote.source

import com.example.newsapp.data.remote.api.CurrentsService
import com.example.newsapp.data.remote.dto.toArticleOrNull
import com.example.newsapp.domain.security.ApiKeyStore

/**
 * currentsapi.services source. Numeric paging (`page_number`/`page_size`) encoded in the cursor.
 * Its category vocabulary already covers the shared categories, so they pass through unchanged.
 */
class CurrentsSource(
    private val service: CurrentsService,
    private val apiKeyStore: ApiKeyStore
) : NewsSource {

    override val id: String = ID

    override suspend fun getNews(category: String?, cursor: String?, pageSize: Int): NewsPage {
        val page = pageOf(cursor)
        val articles = service.getLatest(
            apiKey = apiKey(),
            category = category?.takeIf { it.isNotBlank() },
            pageNumber = page,
            pageSize = pageSize
        ).news.orEmpty().mapNotNull { it.toArticleOrNull() }
        return NewsPage(articles, nextPageCursor(page, articles))
    }

    override suspend fun searchNews(query: String, cursor: String?, pageSize: Int): NewsPage {
        val page = pageOf(cursor)
        val articles = service.searchNews(
            apiKey = apiKey(),
            keywords = query,
            pageNumber = page,
            pageSize = pageSize
        ).news.orEmpty().mapNotNull { it.toArticleOrNull() }
        return NewsPage(articles, nextPageCursor(page, articles))
    }

    private suspend fun apiKey(): String =
        apiKeyStore.getKey(ID).ifBlank { throw MissingApiKeyException(ID) }

    companion object {
        const val ID = "currents"
    }
}
