package com.skystone1000.briefly.data.remote.source

import com.skystone1000.briefly.data.remote.api.NewsApiService
import com.skystone1000.briefly.data.remote.dto.toArticleOrNull
import com.skystone1000.briefly.domain.security.ApiKeyStore

class NewsApiSource(
    private val service: NewsApiService,
    private val apiKeyStore: ApiKeyStore
) : NewsSource {

    override val id: String = ID

    override suspend fun getNews(category: String?, cursor: String?, pageSize: Int): NewsPage {
        val page = pageOf(cursor)
        val articles = service.getTopHeadlines(
            category = category,
            page = page,
            pageSize = pageSize,
            apiKey = apiKey()
        ).articles.orEmpty().mapNotNull { it.toArticleOrNull() }
        return NewsPage(articles, nextPageCursor(page, articles))
    }

    override suspend fun searchNews(query: String, cursor: String?, pageSize: Int): NewsPage {
        val page = pageOf(cursor)
        val articles = service.searchNews(
            query = query,
            page = page,
            pageSize = pageSize,
            apiKey = apiKey()
        ).articles.orEmpty().mapNotNull { it.toArticleOrNull() }
        return NewsPage(articles, nextPageCursor(page, articles))
    }

    private suspend fun apiKey(): String =
        apiKeyStore.getKey(ID).ifBlank { throw MissingApiKeyException(ID) }

    companion object {
        const val ID = "newsapi"
    }
}
