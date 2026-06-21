package com.example.newsapp.di

import com.example.newsapp.data.local.NewsDao
import com.example.newsapp.data.local.ReadingHistoryDao
import com.example.newsapp.data.remote.source.NewsSourceProvider
import com.example.newsapp.data.repository.NewsRepositoryImpl
import com.example.newsapp.domain.repository.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideNewsRepository(
        newsSourceProvider: NewsSourceProvider,
        newsDao: NewsDao,
        readingHistoryDao: ReadingHistoryDao
    ): NewsRepository = NewsRepositoryImpl(newsSourceProvider, newsDao, readingHistoryDao)
}
