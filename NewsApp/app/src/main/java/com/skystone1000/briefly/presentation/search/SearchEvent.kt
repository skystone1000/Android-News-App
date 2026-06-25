package com.skystone1000.briefly.presentation.search

sealed class SearchEvent {
    data class UpdateSearchQuery(val query: String) : SearchEvent()
    data object SearchNews : SearchEvent()
}
