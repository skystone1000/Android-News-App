package com.example.newsapp.data.remote.source

import com.example.newsapp.data.remote.api.NewsDataService
import com.example.newsapp.data.remote.dto.toArticleOrNull
import com.example.newsapp.domain.model.NewsCategories
import com.example.newsapp.domain.security.ApiKeyStore

/**
 * newsdata.io source. Paginates with the provider's opaque `nextPage` token (passed straight
 * through as the cursor). NewsData has no "general" category — it maps to "top".
 */
class NewsDataSource(
    private val service: NewsDataService,
    private val apiKeyStore: ApiKeyStore
) : NewsSource {

    override val id: String = ID

    override suspend fun getNews(category: String?, cursor: String?, pageSize: Int): NewsPage {
        val response = service.getLatest(
            apiKey = apiKey(),
            category = mapCategory(category),
            page = cursor
        )
        val articles = response.results.orEmpty().mapNotNull { it.toArticleOrNull() }
        return NewsPage(articles, response.nextPage)
    }

    override suspend fun searchNews(query: String, cursor: String?, pageSize: Int): NewsPage {
        val response = service.searchNews(apiKey = apiKey(), query = query, page = cursor)
        val articles = response.results.orEmpty().mapNotNull { it.toArticleOrNull() }
        return NewsPage(articles, response.nextPage)
    }

    private fun mapCategory(category: String?): String? = when {
        category.isNullOrBlank() -> null
        category == NewsCategories.GENERAL -> "top"
        else -> category
    }

    private suspend fun apiKey(): String =
        apiKeyStore.getKey(ID).ifBlank { throw MissingApiKeyException(ID) }

    companion object {
        const val ID = "newsdata"
    }
}
