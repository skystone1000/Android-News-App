package com.example.newsapp.di

import com.example.newsapp.data.remote.api.CurrentsService
import com.example.newsapp.data.remote.api.GNewsService
import com.example.newsapp.data.remote.api.MediastackService
import com.example.newsapp.data.remote.api.NewsApiService
import com.example.newsapp.data.remote.api.NewsDataService
import com.example.newsapp.data.remote.source.CurrentsSource
import com.example.newsapp.data.remote.source.GNewsSource
import com.example.newsapp.data.remote.source.MediastackSource
import com.example.newsapp.data.remote.source.NewsApiSource
import com.example.newsapp.data.remote.source.NewsDataSource
import com.example.newsapp.data.remote.source.NewsSource
import com.example.newsapp.domain.security.ApiKeyStore
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
    fun provideNewsApiSource(service: NewsApiService, apiKeyStore: ApiKeyStore): NewsSource =
        NewsApiSource(service, apiKeyStore)

    @Provides
    @IntoMap
    @StringKey(GNewsSource.ID)
    @Singleton
    fun provideGNewsSource(service: GNewsService, apiKeyStore: ApiKeyStore): NewsSource =
        GNewsSource(service, apiKeyStore)

    @Provides
    @IntoMap
    @StringKey(NewsDataSource.ID)
    @Singleton
    fun provideNewsDataSource(service: NewsDataService, apiKeyStore: ApiKeyStore): NewsSource =
        NewsDataSource(service, apiKeyStore)

    @Provides
    @IntoMap
    @StringKey(CurrentsSource.ID)
    @Singleton
    fun provideCurrentsSource(service: CurrentsService, apiKeyStore: ApiKeyStore): NewsSource =
        CurrentsSource(service, apiKeyStore)

    @Provides
    @IntoMap
    @StringKey(MediastackSource.ID)
    @Singleton
    fun provideMediastackSource(service: MediastackService, apiKeyStore: ApiKeyStore): NewsSource =
        MediastackSource(service, apiKeyStore)
}
