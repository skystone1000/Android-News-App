package com.example.newsapp.di

import com.example.newsapp.domain.repository.NewsRepository
import com.example.newsapp.domain.usecases.news.ClearHistory
import com.example.newsapp.domain.usecases.news.DeleteArticle
import com.example.newsapp.domain.usecases.news.GetHistory
import com.example.newsapp.domain.usecases.news.GetNews
import com.example.newsapp.domain.usecases.news.NewsUseCases
import com.example.newsapp.domain.usecases.news.RecordHistory
import com.example.newsapp.domain.usecases.news.SearchNews
import com.example.newsapp.domain.usecases.news.SelectArticle
import com.example.newsapp.domain.usecases.news.SelectArticles
import com.example.newsapp.domain.usecases.news.UpsertArticle
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
