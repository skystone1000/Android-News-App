package com.skystone1000.briefly.di

import com.skystone1000.briefly.data.remote.api.CurrentsService
import com.skystone1000.briefly.data.remote.api.GNewsService
import com.skystone1000.briefly.data.remote.api.MediastackService
import com.skystone1000.briefly.data.remote.api.NewsApiService
import com.skystone1000.briefly.data.remote.api.NewsDataService
import com.skystone1000.briefly.data.remote.source.CurrentsSource
import com.skystone1000.briefly.data.remote.source.GNewsSource
import com.skystone1000.briefly.data.remote.source.MediastackSource
import com.skystone1000.briefly.data.remote.source.NewsApiSource
import com.skystone1000.briefly.data.remote.source.NewsDataSource
import com.skystone1000.briefly.data.remote.source.NewsSource
import com.skystone1000.briefly.domain.security.ApiKeyStore
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
