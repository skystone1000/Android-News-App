package com.example.newsapp.data.remote.dto

import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.model.Source
import com.google.gson.annotations.SerializedName

/** Wire format for mediastack.com (`data[]` root, offset pagination). */
data class MediastackResponse(
    val pagination: MediastackPagination?,
    val data: List<MediastackArticleDto>?
)

data class MediastackPagination(
    val limit: Int?,
    val offset: Int?,
    val count: Int?,
    val total: Int?
)

data class MediastackArticleDto(
    val author: String?,
    val title: String?,
    val description: String?,
    val url: String?,
    val source: String?,
    val image: String?,
    val category: String?,
    @SerializedName("published_at") val publishedAt: String?
)

fun MediastackArticleDto.toArticleOrNull(): Article? {
    val articleUrl = url
    if (articleUrl.isNullOrBlank() || title.isNullOrBlank()) return null
    return Article(
        source = Source(id = source.orEmpty(), name = source.orEmpty()),
        author = author.orEmpty(),
        title = title,
        description = description.orEmpty(),
        url = articleUrl,
        urlToImage = image.orEmpty(),
        publishedAt = publishedAt.orEmpty(),
        content = description.orEmpty()
    )
}
