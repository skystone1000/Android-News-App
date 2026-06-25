package com.skystone1000.briefly.presentation.bookmark

import com.skystone1000.briefly.domain.model.Article

data class BookmarkState(
    val articles: List<Article> = emptyList(),
    /** URLs of articles the user has already opened (from reading history) → counts as "read". */
    val readUrls: Set<String> = emptySet()
)
