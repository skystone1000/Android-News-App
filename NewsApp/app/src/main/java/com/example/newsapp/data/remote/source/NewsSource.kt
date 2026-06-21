package com.example.newsapp.data.remote.source

import com.example.newsapp.domain.model.Article

/**
 * Provider-agnostic access to news. Each backend (NewsAPI, GNews, …) implements this;
 * the rest of the app never depends on a concrete provider. Adding a provider = adding
 * one implementation + one Hilt map binding.
 */
interface NewsSource {

    /** Stable identifier used for runtime selection (e.g. "newsapi", "gnews"). */
    val id: String

    suspend fun getNews(category: String?, page: Int, pageSize: Int): List<Article>

    suspend fun searchNews(query: String, page: Int, pageSize: Int): List<Article>
}
