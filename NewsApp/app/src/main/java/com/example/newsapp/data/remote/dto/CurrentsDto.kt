package com.example.newsapp.data.remote.dto

import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.model.Source

/** Wire format for currentsapi.services (`news[]` root). */
data class CurrentsResponse(
    val status: String?,
    val news: List<CurrentsArticleDto>?
)

data class CurrentsArticleDto(
    val id: String?,
    val title: String?,
    val description: String?,
    val url: String?,
    val author: String?,
    val image: String?,
    val published: String?
)

fun CurrentsArticleDto.toArticleOrNull(): Article? {
    val articleUrl = url
    if (articleUrl.isNullOrBlank() || title.isNullOrBlank()) return null
    val imageUrl = image?.takeIf { it.startsWith("http") }.orEmpty()
    return Article(
        source = Source(id = "currents", name = "Currents"),
        author = author.orEmpty(),
        title = title,
        description = description.orEmpty(),
        url = articleUrl,
        urlToImage = imageUrl,
        publishedAt = published.orEmpty(),
        content = description.orEmpty()
    )
}
