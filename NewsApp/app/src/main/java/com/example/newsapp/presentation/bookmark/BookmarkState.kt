package com.example.newsapp.presentation.bookmark

import com.example.newsapp.domain.model.Article

data class BookmarkState(
    val articles: List<Article> = emptyList(),
    /** URLs of articles the user has already opened (from reading history) → counts as "read". */
    val readUrls: Set<String> = emptySet()
)
