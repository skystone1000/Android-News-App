package com.example.newsapp.data.ai

import com.example.newsapp.data.ai.dto.ClaudeRequest
import com.example.newsapp.data.ai.dto.ClaudeResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Retrofit binding for Anthropic's Messages API. The API key is passed per-request
 * via the `x-api-key` header (sourced from BuildConfig / a proxy).
 */
interface ClaudeService {

    @Headers("anthropic-version: 2023-06-01", "content-type: application/json")
    @POST("v1/messages")
    suspend fun createMessage(
        @Header("x-api-key") apiKey: String,
        @Body request: ClaudeRequest
    ): ClaudeResponse

    companion object {
        const val BASE_URL = "https://api.anthropic.com/"
    }
}
