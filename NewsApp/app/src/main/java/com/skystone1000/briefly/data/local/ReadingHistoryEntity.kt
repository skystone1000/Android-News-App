package com.skystone1000.briefly.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.skystone1000.briefly.domain.model.Article
import com.skystone1000.briefly.domain.model.Source

/**
 * Room row recording that the user opened an article. Keyed by [url] so re-opening an
 * article refreshes its [readAt] instead of creating a duplicate. Mirrors the flattened
 * shape of [ArticleEntity] so a history row can be rendered/navigated like any article.
 */
@Entity(tableName = "reading_history")
data class ReadingHistoryEntity(
    @PrimaryKey val url: String,
    val sourceId: String,
    val sourceName: String,
    val author: String,
    val title: String,
    val description: String,
    val urlToImage: String,
    val publishedAt: String,
    val content: String,
    val readAt: Long
)

fun ReadingHistoryEntity.toArticle(): Article = Article(
    source = Source(id = sourceId, name = sourceName),
    author = author,
    title = title,
    description = description,
    url = url,
    urlToImage = urlToImage,
    publishedAt = publishedAt,
    content = content
)

fun Article.toHistoryEntity(readAt: Long): ReadingHistoryEntity = ReadingHistoryEntity(
    url = url,
    sourceId = source.id,
    sourceName = source.name,
    author = author,
    title = title,
    description = description,
    urlToImage = urlToImage,
    publishedAt = publishedAt,
    content = content,
    readAt = readAt
)
