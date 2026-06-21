package com.example.newsapp.domain.model

import java.io.Serializable

/**
 * A news article. Pure-Kotlin domain model shared across layers.
 * [url] is the stable identity (used as the bookmark primary key).
 * `Serializable` (pure JVM) lets it ride through Compose navigation.
 */
data class Article(
    val source: Source,
    val author: String,
    val title: String,
    val description: String,
    val url: String,
    val urlToImage: String,
    val publishedAt: String,
    val content: String
) : Serializable {
    private companion object {
        private const val serialVersionUID: Long = 1L
    }
}
