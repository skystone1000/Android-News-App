package com.skystone1000.briefly.di

import com.skystone1000.briefly.BuildConfig
import com.skystone1000.briefly.data.ai.ClaudeAiGateway
import com.skystone1000.briefly.data.ai.ClaudeService
import com.skystone1000.briefly.data.local.AiInsightDao
import com.skystone1000.briefly.data.local.NewsDatabase
import com.skystone1000.briefly.domain.ai.AiGateway
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
