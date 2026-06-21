package com.example.newsapp.data.remote.dto

import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.model.Source

/** Wire format for newsapi.org. */
data class NewsApiResponse(
    val status: String?,
    val totalResults: Int?,
    val articles: List<NewsApiArticleDto>?
)

data class NewsApiArticleDto(
    val source: NewsApiSourceDto?,
    val author: String?,
    val title: String?,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val publishedAt: String?,
    val content: String?
)

data class NewsApiSourceDto(
    val id: String?,
    val name: String?
)

fun NewsApiArticleDto.toArticleOrNull(): Article? {
    val articleUrl = url
    if (articleUrl.isNullOrBlank() || title.isNullOrBlank()) return null
    return Article(
        source = Source(id = source?.id ?: source?.name.orEmpty(), name = source?.name.orEmpty()),
        author = author.orEmpty(),
        title = title,
        description = description.orEmpty(),
        url = articleUrl,
        urlToImage = urlToImage.orEmpty(),
        publishedAt = publishedAt.orEmpty(),
        content = content.orEmpty()
    )
}
