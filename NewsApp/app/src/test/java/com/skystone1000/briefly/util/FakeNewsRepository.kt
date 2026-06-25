package com.skystone1000.briefly.util

import androidx.paging.PagingData
import com.skystone1000.briefly.domain.model.Article
import com.skystone1000.briefly.domain.model.Source
import com.skystone1000.briefly.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

/** In-memory [NewsRepository] for ViewModel/use-case tests. */
class FakeNewsRepository : NewsRepository {

    val bookmarks = mutableListOf<Article>()
    private val bookmarksFlow = MutableStateFlow<List<Article>>(emptyList())

    val history = mutableListOf<Article>()
    private val historyFlow = MutableStateFlow<List<Article>>(emptyList())

    override fun getNews(category: String?): Flow<PagingData<Article>> = flowOf(PagingData.empty())

    override fun searchNews(query: String): Flow<PagingData<Article>> = flowOf(PagingData.empty())

    override fun getArticles(): Flow<List<Article>> = bookmarksFlow

    override suspend fun getArticle(url: String): Article? = bookmarks.find { it.url == url }

    override suspend fun upsertArticle(article: Article) {
        bookmarks.removeAll { it.url == article.url }
        bookmarks.add(article)
        bookmarksFlow.value = bookmarks.toList()
    }

    override suspend fun deleteArticle(article: Article) {
        bookmarks.removeAll { it.url == article.url }
        bookmarksFlow.value = bookmarks.toList()
    }

    override suspend fun recordHistory(article: Article) {
        history.removeAll { it.url == article.url }
        history.add(0, article)
        historyFlow.value = history.toList()
    }

    override fun getHistory(): Flow<List<Article>> = historyFlow

    override suspend fun clearHistory() {
        history.clear()
        historyFlow.value = emptyList()
    }
}

fun testArticle(url: String = "https://example.com/a") = Article(
    source = Source(id = "src", name = "Source"),
    author = "Author",
    title = "Title",
    description = "Description",
    url = url,
    urlToImage = "",
    publishedAt = "2026-06-21",
    content = "Content"
)
