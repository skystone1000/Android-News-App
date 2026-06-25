package com.skystone1000.briefly.data.remote.api

import com.skystone1000.briefly.data.remote.dto.CurrentsResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit binding for currentsapi.services. Per the Currents OpenAPI spec, `v1/latest-news`
 * accepts **only** `language` (no category or pagination), and `v1/search` accepts `keywords`,
 * `language`, `category`, `country` and date filters (no pagination). Sending unsupported params
 * (e.g. `page_number`/`page_size`) makes the API reject the request with HTTP 400.
 */
interface CurrentsService {

    @GET("v1/latest-news")
    suspend fun getLatest(
        @Query("apiKey") apiKey: String,
        @Query("language") language: String = "en"
    ): CurrentsResponse

    @GET("v1/search")
    suspend fun searchNews(
        @Query("apiKey") apiKey: String,
        @Query("keywords") keywords: String,
        @Query("language") language: String = "en"
    ): CurrentsResponse

    companion object {
        const val BASE_URL = "https://api.currentsapi.services/"
    }
}
