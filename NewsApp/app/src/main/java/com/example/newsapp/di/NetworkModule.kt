package com.example.newsapp.di

import com.example.newsapp.BuildConfig
import com.example.newsapp.data.remote.api.CurrentsService
import com.example.newsapp.data.remote.api.GNewsService
import com.example.newsapp.data.remote.api.MediastackService
import com.example.newsapp.data.remote.api.NewsApiService
import com.example.newsapp.data.remote.api.NewsDataService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
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
