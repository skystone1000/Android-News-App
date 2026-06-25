package com.skystone1000.briefly.presentation.details

import com.skystone1000.briefly.domain.model.Article

sealed class DetailsEvent {
    data class UpsertDeleteArticle(val article: Article) : DetailsEvent()
    data object RemoveSideEffect : DetailsEvent()
}
