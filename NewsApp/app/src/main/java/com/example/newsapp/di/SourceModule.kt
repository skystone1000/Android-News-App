package com.example.newsapp.di

import com.example.newsapp.BuildConfig
import com.example.newsapp.data.remote.api.GNewsService
import com.example.newsapp.data.remote.api.NewsApiService
import com.example.newsapp.data.remote.source.GNewsSource
import com.example.newsapp.data.remote.source.NewsApiSource
import com.example.newsapp.data.remote.source.NewsSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import javax.inject.Singleton

/**
 * Registers each [NewsSource] into a map keyed by its id. Adding a new provider is one
 * `@Provides @IntoMap @StringKey(...)` binding here — nothing else changes.
 */
@Module
@InstallIn(SingletonComponent::class)
object SourceModule {

    @Provides
    @IntoMap
    @StringKey(NewsApiSource.ID)
    @Singleton
    fun provideNewsApiSource(service: NewsApiService): NewsSource =
        NewsApiSource(service, BuildConfig.NEWS_API_KEY)

    @Provides
    @IntoMap
    @StringKey(GNewsSource.ID)
    @Singleton
    fun provideGNewsSource(service: GNewsService): NewsSource =
        GNewsSource(service, BuildConfig.GNEWS_API_KEY)
}
