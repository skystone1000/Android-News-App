package com.example.newsapp.data.local

import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.model.Source

fun ArticleEntity.toArticle(): Article = Article(
    source = Source(id = sourceId, name = sourceName),
    author = author,
    title = title,
    description = description,
    url = url,
    urlToImage = urlToImage,
    publishedAt = publishedAt,
    content = content
)

fun Article.toEntity(): ArticleEntity = ArticleEntity(
    url = url,
    sourceId = source.id,
    sourceName = source.name,
    author = author,
    title = title,
    description = description,
    urlToImage = urlToImage,
    publishedAt = publishedAt,
    content = content
)
