package com.example.newsapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Cached AI insight, keyed by article URL. Tags are stored as a newline-joined string. */
@Entity(tableName = "ai_insights")
data class AiInsightEntity(
    @PrimaryKey val articleUrl: String,
    val summary: String,
    val sentiment: String,
    val tags: String
)
