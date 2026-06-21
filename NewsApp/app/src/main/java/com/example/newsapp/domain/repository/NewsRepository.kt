package com.example.newsapp.domain.repository

import androidx.paging.PagingData
import com.example.newsapp.domain.model.Article
import kotlinx.coroutines.flow.Flow

/**
 * Single contract the presentation/domain layers depend on. Implemented in the data
 * layer over a runtime-selectable remote source plus a Room-backed bookmark store.
 */
interface NewsRepository {

    /** Paged headlines for an optional [category] from the active source. */
    fun getNews(category: String?): Flow<PagingData<Article>>

    /** Paged search results for [query] from the active source. */
    fun searchNews(query: String): Flow<PagingData<Article>>

    /** All bookmarked articles, newest first. */
    fun getArticles(): Flow<List<Article>>

    suspend fun getArticle(url: String): Article?

    suspend fun upsertArticle(article: Article)

    suspend fun deleteArticle(article: Article)
}
