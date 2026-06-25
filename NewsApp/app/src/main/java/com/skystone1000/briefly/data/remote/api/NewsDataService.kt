package com.skystone1000.briefly.data.remote.api

import com.skystone1000.briefly.data.remote.dto.NewsDataResponse
import retrofit2.http.GET
import retrofit2.http.Query

/** Retrofit binding for newsdata.io. `page` is the opaque cursor token from `nextPage`. */
interface NewsDataService {

    @GET("api/1/latest")
    suspend fun getLatest(
        @Query("apikey") apiKey: String,
        @Query("category") category: String?,
        @Query("page") page: String?,
        @Query("language") language: String = "en"
    ): NewsDataResponse

    @GET("api/1/latest")
    suspend fun searchNews(
        @Query("apikey") apiKey: String,
        @Query("q") query: String,
        @Query("page") page: String?,
        @Query("language") language: String = "en"
    ): NewsDataResponse

    companion object {
        const val BASE_URL = "https://newsdata.io/"
    }
}
