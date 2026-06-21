package com.example.newsapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.newsapp.data.local.NewsDao
import com.example.newsapp.data.local.toArticle
import com.example.newsapp.data.local.toEntity
import com.example.newsapp.data.remote.NewsPagingSource
import com.example.newsapp.data.remote.source.NewsSourceProvider
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NewsRepositoryImpl(
    private val newsSourceProvider: NewsSourceProvider,
    private val newsDao: NewsDao
) : NewsRepository {

    override fun getNews(category: String?): Flow<PagingData<Article>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        pagingSourceFactory = {
            NewsPagingSource(newsSourceProvider.activeSource(), category = category, query = null)
        }
    ).flow

    override fun searchNews(query: String): Flow<PagingData<Article>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        pagingSourceFactory = {
            NewsPagingSource(newsSourceProvider.activeSource(), category = null, query = query)
        }
    ).flow

    override fun getArticles(): Flow<List<Article>> =
        newsDao.getArticles().map { entities -> entities.map { it.toArticle() } }

    override suspend fun getArticle(url: String): Article? =
        newsDao.getArticle(url)?.toArticle()

    override suspend fun upsertArticle(article: Article) =
        newsDao.upsert(article.toEntity())

    override suspend fun deleteArticle(article: Article) =
        newsDao.delete(article.toEntity())

    private companion object {
        const val PAGE_SIZE = 20
    }
}
