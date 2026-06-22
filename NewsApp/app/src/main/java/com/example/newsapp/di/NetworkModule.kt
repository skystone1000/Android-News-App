package com.example.newsapp.di

import com.example.newsapp.BuildConfig
import com.example.newsapp.data.remote.api.CurrentsService
import com.example.newsapp.data.remote.api.GNewsService
import com.example.newsapp.data.remote.RedactingLoggingInterceptor
import com.example.newsapp.data.remote.UsageInterceptor
import com.example.newsapp.data.remote.api.MediastackService
import com.example.newsapp.data.remote.api.NewsApiService
import com.example.newsapp.data.remote.api.NewsDataService
import com.example.newsapp.domain.usage.ApiUsageStore
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
    fun provideOkHttpClient(usageStore: ApiUsageStore): OkHttpClient {
        val usageScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        return OkHttpClient.Builder()
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
