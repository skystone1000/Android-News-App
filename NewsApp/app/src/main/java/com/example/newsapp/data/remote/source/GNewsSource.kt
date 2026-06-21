package com.example.newsapp.data.remote.source

import com.example.newsapp.data.remote.api.GNewsService
import com.example.newsapp.data.remote.dto.toArticleOrNull
import com.example.newsapp.domain.model.Article

class GNewsSource(
    private val service: GNewsService,
    private val apiKey: String
) : NewsSource {

    override val id: String = ID

    override suspend fun getNews(category: String?, page: Int, pageSize: Int): List<Article> =
        service.getTopHeadlines(
            category = category,
            page = page,
            max = pageSize,
            apiKey = apiKey
        ).articles.orEmpty().mapNotNull { it.toArticleOrNull() }

    override suspend fun searchNews(query: String, page: Int, pageSize: Int): List<Article> =
        service.searchNews(
            query = query,
            page = page,
            max = pageSize,
            apiKey = apiKey
        ).articles.orEmpty().mapNotNull { it.toArticleOrNull() }

    companion object {
        const val ID = "gnews"
    }
}
