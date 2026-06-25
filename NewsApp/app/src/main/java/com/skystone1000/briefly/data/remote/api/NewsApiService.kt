package com.skystone1000.briefly.data.remote.api

import com.skystone1000.briefly.data.remote.dto.NewsApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

/** Retrofit binding for newsapi.org. */
interface NewsApiService {

    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("category") category: String?,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("apiKey") apiKey: String,
        @Query("country") country: String = "us"
    ): NewsApiResponse

    @GET("v2/everything")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("apiKey") apiKey: String
    ): NewsApiResponse

    companion object {
        const val BASE_URL = "https://newsapi.org/"
    }
}
