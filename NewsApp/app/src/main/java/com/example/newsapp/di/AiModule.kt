package com.example.newsapp.di

import com.example.newsapp.BuildConfig
import com.example.newsapp.data.ai.ClaudeAiGateway
import com.example.newsapp.data.ai.ClaudeService
import com.example.newsapp.data.local.AiInsightDao
import com.example.newsapp.data.local.NewsDatabase
import com.example.newsapp.domain.ai.AiGateway
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiModule {

    @Provides
    @Singleton
    fun provideClaudeService(client: OkHttpClient): ClaudeService {
        // Use the proxy if configured (recommended for production — keeps the key off-device),
        // otherwise call the Anthropic API directly with the BuildConfig key (dev only).
        val baseUrl = BuildConfig.CLAUDE_PROXY_URL.ifBlank { ClaudeService.BASE_URL }
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ClaudeService::class.java)
    }

    @Provides
    @Singleton
    fun provideAiInsightDao(database: NewsDatabase): AiInsightDao = database.aiInsightDao

    @Provides
    @Singleton
    fun provideAiGateway(
        service: ClaudeService,
        aiInsightDao: AiInsightDao
    ): AiGateway = ClaudeAiGateway(
        service = service,
        apiKey = BuildConfig.CLAUDE_API_KEY,
        dao = aiInsightDao
    )
}
