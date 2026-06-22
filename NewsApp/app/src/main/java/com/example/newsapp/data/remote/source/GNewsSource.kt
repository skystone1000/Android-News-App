package com.example.newsapp.data.remote.source

import com.example.newsapp.data.remote.api.GNewsService
import com.example.newsapp.data.remote.dto.toArticleOrNull
import com.example.newsapp.domain.security.ApiKeyStore

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
            max = pageSize,
            apiKey = apiKey()
        ).articles.orEmpty().mapNotNull { it.toArticleOrNull() }
        return NewsPage(articles, nextPageCursor(page, articles))
    }

    override suspend fun searchNews(query: String, cursor: String?, pageSize: Int): NewsPage {
        val page = pageOf(cursor)
        val articles = service.searchNews(
            query = query,
            page = page,
            max = pageSize,
            apiKey = apiKey()
        ).articles.orEmpty().mapNotNull { it.toArticleOrNull() }
        return NewsPage(articles, nextPageCursor(page, articles))
    }

    private suspend fun apiKey(): String =
        apiKeyStore.getKey(ID).ifBlank { throw MissingApiKeyException(ID) }

    companion object {
        const val ID = "gnews"
    }
}
