package com.skystone1000.briefly.data.remote.api

import com.skystone1000.briefly.data.remote.dto.MediastackResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit binding for mediastack.com. Offset pagination (`offset`/`limit`).
 * NOTE: the free tier is **HTTP only** (no TLS), hence the `http://` base URL.
 */
interface MediastackService {

    @GET("v1/news")
    suspend fun getNews(
        @Query("access_key") accessKey: String,
        @Query("categories") categories: String?,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("languages") languages: String = "en",
        @Query("sort") sort: String = "published_desc"
    ): MediastackResponse

    @GET("v1/news")
    suspend fun searchNews(
        @Query("access_key") accessKey: String,
        @Query("keywords") keywords: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("languages") languages: String = "en",
        @Query("sort") sort: String = "published_desc"
    ): MediastackResponse

    companion object {
        const val BASE_URL = "http://api.mediastack.com/"
    }
}
