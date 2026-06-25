package com.skystone1000.briefly.di

import com.skystone1000.briefly.data.local.NewsDao
import com.skystone1000.briefly.data.local.ReadingHistoryDao
import com.skystone1000.briefly.data.remote.source.NewsSourceProvider
import com.skystone1000.briefly.data.repository.NewsRepositoryImpl
import com.skystone1000.briefly.domain.repository.NewsRepository
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
