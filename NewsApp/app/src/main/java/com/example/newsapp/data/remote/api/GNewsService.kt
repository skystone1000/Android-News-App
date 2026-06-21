package com.example.newsapp.data.remote.api

import com.example.newsapp.data.remote.dto.GNewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

/** Retrofit binding for gnews.io. */
interface GNewsService {

    @GET("api/v4/top-headlines")
    suspend fun getTopHeadlines(
        @Query("category") category: String?,
        @Query("page") page: Int,
        @Query("max") max: Int,
        @Query("apikey") apiKey: String,
        @Query("lang") lang: String = "en"
    ): GNewsResponse

    @GET("api/v4/search")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("max") max: Int,
        @Query("apikey") apiKey: String,
        @Query("lang") lang: String = "en"
    ): GNewsResponse

    companion object {
        const val BASE_URL = "https://gnews.io/"
    }
}
