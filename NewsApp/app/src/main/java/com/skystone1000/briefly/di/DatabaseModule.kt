package com.skystone1000.briefly.di

import android.app.Application
import androidx.room.Room
import com.skystone1000.briefly.data.local.NewsDao
import com.skystone1000.briefly.data.local.NewsDatabase
import com.skystone1000.briefly.data.local.ReadingHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideNewsDatabase(application: Application): NewsDatabase =
        Room.databaseBuilder(
            context = application,
            klass = NewsDatabase::class.java,
            name = NewsDatabase.NAME
        ).fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun provideNewsDao(database: NewsDatabase): NewsDao = database.newsDao

    @Provides
    @Singleton
    fun provideReadingHistoryDao(database: NewsDatabase): ReadingHistoryDao =
        database.readingHistoryDao
}
