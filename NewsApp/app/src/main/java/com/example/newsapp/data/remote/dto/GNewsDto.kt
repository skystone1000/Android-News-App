package com.example.newsapp.data.remote.dto

import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.model.Source

/** Wire format for gnews.io (note: uses `image` and a name-only source). */
data class GNewsResponse(
    val totalArticles: Int?,
    val articles: List<GNewsArticleDto>?
)

data class GNewsArticleDto(
    val title: String?,
    val description: String?,
    val content: String?,
    val url: String?,
    val image: String?,
    val publishedAt: String?,
    val source: GNewsSourceDto?
)

data class GNewsSourceDto(
    val name: String?,
    val url: String?
)

fun GNewsArticleDto.toArticleOrNull(): Article? {
    val articleUrl = url
    if (articleUrl.isNullOrBlank() || title.isNullOrBlank()) return null
    return Article(
        source = Source(id = source?.name.orEmpty(), name = source?.name.orEmpty()),
        author = source?.name.orEmpty(),
        title = title,
        description = description.orEmpty(),
        url = articleUrl,
        urlToImage = image.orEmpty(),
        publishedAt = publishedAt.orEmpty(),
        content = content.orEmpty()
    )
}
