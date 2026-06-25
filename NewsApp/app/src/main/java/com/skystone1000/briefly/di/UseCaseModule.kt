package com.skystone1000.briefly.di

import com.skystone1000.briefly.domain.repository.NewsRepository
import com.skystone1000.briefly.domain.usecases.news.ClearHistory
import com.skystone1000.briefly.domain.usecases.news.DeleteArticle
import com.skystone1000.briefly.domain.usecases.news.GetHistory
import com.skystone1000.briefly.domain.usecases.news.GetNews
import com.skystone1000.briefly.domain.usecases.news.NewsUseCases
import com.skystone1000.briefly.domain.usecases.news.RecordHistory
import com.skystone1000.briefly.domain.usecases.news.SearchNews
import com.skystone1000.briefly.domain.usecases.news.SelectArticle
import com.skystone1000.briefly.domain.usecases.news.SelectArticles
import com.skystone1000.briefly.domain.usecases.news.UpsertArticle
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideNewsUseCases(repository: NewsRepository): NewsUseCases = NewsUseCases(
        getNews = GetNews(repository),
        searchNews = SearchNews(repository),
        upsertArticle = UpsertArticle(repository),
        deleteArticle = DeleteArticle(repository),
        selectArticles = SelectArticles(repository),
        selectArticle = SelectArticle(repository),
        recordHistory = RecordHistory(repository),
        getHistory = GetHistory(repository),
        clearHistory = ClearHistory(repository)
    )
}
