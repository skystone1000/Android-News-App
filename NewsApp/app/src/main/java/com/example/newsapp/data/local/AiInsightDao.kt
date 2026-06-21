package com.example.newsapp.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface AiInsightDao {

    @Upsert
    suspend fun upsert(insight: AiInsightEntity)

    @Query("SELECT * FROM ai_insights WHERE articleUrl = :url")
    suspend fun get(url: String): AiInsightEntity?
}
