package com.skystone1000.briefly.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.skystone1000.briefly.data.local.NewsDao
import com.skystone1000.briefly.data.local.ReadingHistoryDao
import com.skystone1000.briefly.data.local.toArticle
import com.skystone1000.briefly.data.local.toEntity
import com.skystone1000.briefly.data.local.toHistoryEntity
import com.skystone1000.briefly.data.remote.NewsPagingSource
import com.skystone1000.briefly.data.remote.source.NewsSourceProvider
import com.skystone1000.briefly.domain.model.Article
import com.skystone1000.briefly.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NewsRepositoryImpl(
    private val newsSourceProvider: NewsSourceProvider,
    private val newsDao: NewsDao,
    private val readingHistoryDao: ReadingHistoryDao
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

    override suspend fun recordHistory(article: Article) =
        readingHistoryDao.upsert(article.toHistoryEntity(readAt = System.currentTimeMillis()))

    override fun getHistory(): Flow<List<Article>> =
        readingHistoryDao.getHistory().map { entities -> entities.map { it.toArticle() } }

    override suspend fun clearHistory() = readingHistoryDao.clear()

    private companion object {
        const val PAGE_SIZE = 20
    }
}
