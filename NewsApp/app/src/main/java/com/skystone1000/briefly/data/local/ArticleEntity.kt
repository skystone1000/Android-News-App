package com.skystone1000.briefly.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room row for a bookmarked article. [Source] is flattened into two columns to avoid a
 * type converter; mapping to/from the domain [com.skystone1000.briefly.domain.model.Article]
 * lives in `ArticleMapper.kt`.
 */
@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val url: String,
    val sourceId: String,
    val sourceName: String,
    val author: String,
    val title: String,
    val description: String,
    val urlToImage: String,
    val publishedAt: String,
    val content: String
)
