package com.example.newsapp.data.remote.api

import com.example.newsapp.data.remote.dto.CurrentsResponse
import retrofit2.http.GET
import retrofit2.http.Query

/** Retrofit binding for currentsapi.services. Numeric paging via `page_number`. */
interface CurrentsService {

    @GET("v1/latest-news")
    suspend fun getLatest(
        @Query("apiKey") apiKey: String,
        @Query("category") category: String?,
        @Query("page_number") pageNumber: Int,
        @Query("page_size") pageSize: Int,
        @Query("language") language: String = "en"
    ): CurrentsResponse

    @GET("v1/search")
    suspend fun searchNews(
        @Query("apiKey") apiKey: String,
        @Query("keywords") keywords: String,
        @Query("page_number") pageNumber: Int,
        @Query("page_size") pageSize: Int,
        @Query("language") language: String = "en"
    ): CurrentsResponse

    companion object {
        const val BASE_URL = "https://api.currentsapi.services/"
    }
}
