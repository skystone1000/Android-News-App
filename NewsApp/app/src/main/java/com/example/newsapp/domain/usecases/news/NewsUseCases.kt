package com.example.newsapp.domain.usecases.news

import androidx.paging.PagingData
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow

/** Grouped news use cases, injected as a single dependency into ViewModels. */
data class NewsUseCases(
    val getNews: GetNews,
    val searchNews: SearchNews,
    val upsertArticle: UpsertArticle,
    val deleteArticle: DeleteArticle,
    val selectArticles: SelectArticles,
    val selectArticle: SelectArticle
)

class GetNews(private val repository: NewsRepository) {
    operator fun invoke(category: String?): Flow<PagingData<Article>> = repository.getNews(category)
}

class SearchNews(private val repository: NewsRepository) {
    operator fun invoke(query: String): Flow<PagingData<Article>> = repository.searchNews(query)
}

class UpsertArticle(private val repository: NewsRepository) {
    suspend operator fun invoke(article: Article) = repository.upsertArticle(article)
}

class DeleteArticle(private val repository: NewsRepository) {
    suspend operator fun invoke(article: Article) = repository.deleteArticle(article)
}

/** All bookmarked articles. */
class SelectArticles(private val repository: NewsRepository) {
    operator fun invoke(): Flow<List<Article>> = repository.getArticles()
}

/** A single bookmarked article by url, or null if not bookmarked. */
class SelectArticle(private val repository: NewsRepository) {
    suspend operator fun invoke(url: String): Article? = repository.getArticle(url)
}
