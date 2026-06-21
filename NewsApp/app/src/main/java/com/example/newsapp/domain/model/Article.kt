package com.example.newsapp.domain.model

/**
 * A news article. Pure-Kotlin domain model shared across layers.
 * [url] is the stable identity (used as the bookmark primary key).
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
)
