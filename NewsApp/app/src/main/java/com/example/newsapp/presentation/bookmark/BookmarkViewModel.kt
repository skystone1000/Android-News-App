package com.example.newsapp.presentation.bookmark

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.domain.usecases.news.NewsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    newsUseCases: NewsUseCases
) : ViewModel() {

    var state by mutableStateOf(BookmarkState())
        private set

    init {
        newsUseCases.selectArticles()
            .onEach { articles -> state = state.copy(articles = articles) }
            .launchIn(viewModelScope)

        // Reading history drives the read/unread state of saved articles.
        newsUseCases.getHistory()
            .onEach { history -> state = state.copy(readUrls = history.mapTo(mutableSetOf()) { it.url }) }
            .launchIn(viewModelScope)
    }
}
