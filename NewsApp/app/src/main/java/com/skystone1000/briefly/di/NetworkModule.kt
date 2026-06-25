package com.skystone1000.briefly.di

import com.skystone1000.briefly.BuildConfig
import com.skystone1000.briefly.data.remote.api.CurrentsService
import com.skystone1000.briefly.data.remote.api.GNewsService
import com.skystone1000.briefly.data.remote.MockInterceptor
import com.skystone1000.briefly.data.remote.RedactingLoggingInterceptor
import com.skystone1000.briefly.data.remote.UsageInterceptor
import com.skystone1000.briefly.data.remote.api.MediastackService
import com.skystone1000.briefly.data.remote.api.NewsApiService
import com.skystone1000.briefly.data.remote.api.NewsDataService
import com.skystone1000.briefly.domain.usage.ApiUsageStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        usageStore: ApiUsageStore,
        mockInterceptor: MockInterceptor,
    ): OkHttpClient {
        val usageScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        return OkHttpClient.Builder()
            // First: debug capture / offline replay (no-op in release). Runs before usage
            // counting so replayed calls don't consume the free-tier quota.
            .addInterceptor(mockInterceptor)
            .addInterceptor(UsageInterceptor(usageStore, usageScope))
            // Custom logger that redacts the API key from query params (debug only).
            .addInterceptor(RedactingLoggingInterceptor(enabled = BuildConfig.DEBUG))
            .build()
    }

    @Provides
    @Singleton
    fun provideNewsApiService(client: OkHttpClient): NewsApiService =
        Retrofit.Builder()
            .baseUrl(NewsApiService.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiService::class.java)

    @Provides
    @Singleton
    fun provideGNewsService(client: OkHttpClient): GNewsService =
        Retrofit.Builder()
            .baseUrl(GNewsService.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GNewsService::class.java)

    @Provides
    @Singleton
    fun provideNewsDataService(client: OkHttpClient): NewsDataService =
        Retrofit.Builder()
            .baseUrl(NewsDataService.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsDataService::class.java)

    @Provides
    @Singleton
    fun provideCurrentsService(client: OkHttpClient): CurrentsService =
        Retrofit.Builder()
            .baseUrl(CurrentsService.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrentsService::class.java)

    @Provides
    @Singleton
    fun provideMediastackService(client: OkHttpClient): MediastackService =
        Retrofit.Builder()
            .baseUrl(MediastackService.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MediastackService::class.java)
}
