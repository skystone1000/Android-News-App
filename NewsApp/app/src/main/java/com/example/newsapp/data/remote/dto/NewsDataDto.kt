package com.example.newsapp.data.remote.dto

import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.model.Source
import com.google.gson.annotations.SerializedName

/** Wire format for newsdata.io (`api/1/latest`). Pagination is an opaque `nextPage` token. */
data class NewsDataResponse(
    val status: String?,
    val totalResults: Int?,
    val results: List<NewsDataArticleDto>?,
    val nextPage: String?
)

data class NewsDataArticleDto(
    val title: String?,
    val link: String?,
    val description: String?,
    val content: String?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("pubDate") val pubDate: String?,
    @SerializedName("source_id") val sourceId: String?,
    @SerializedName("source_name") val sourceName: String?,
    val creator: List<String>?
)

fun NewsDataArticleDto.toArticleOrNull(): Article? {
    val articleUrl = link
    if (articleUrl.isNullOrBlank() || title.isNullOrBlank()) return null
    return Article(
        source = Source(id = sourceId.orEmpty(), name = sourceName ?: sourceId.orEmpty()),
        author = creator?.firstOrNull().orEmpty(),
        title = title,
        description = description.orEmpty(),
        url = articleUrl,
        urlToImage = imageUrl.orEmpty(),
        publishedAt = pubDate.orEmpty(),
        content = content.orEmpty()
    )
}
