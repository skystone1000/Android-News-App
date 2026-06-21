package com.example.newsapp.presentation.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.newsapp.domain.usecases.news.NewsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val newsUseCases: NewsUseCases
) : ViewModel() {

    var state by mutableStateOf(SearchState())
        private set

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.UpdateSearchQuery -> state = state.copy(searchQuery = event.query)
            is SearchEvent.SearchNews -> searchNews()
        }
    }

    private fun searchNews() {
        val query = state.searchQuery.trim()
        if (query.isEmpty()) return
        val articles = newsUseCases.searchNews(query).cachedIn(viewModelScope)
        state = state.copy(articles = articles)
    }
}
