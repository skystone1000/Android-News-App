package com.example.newsapp.presentation.search

sealed class SearchEvent {
    data class UpdateSearchQuery(val query: String) : SearchEvent()
    data object SearchNews : SearchEvent()
}
