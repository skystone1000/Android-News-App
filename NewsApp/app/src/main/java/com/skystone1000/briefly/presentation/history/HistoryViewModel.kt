package com.skystone1000.briefly.presentation.history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skystone1000.briefly.domain.model.Article
import com.skystone1000.briefly.domain.usecases.news.NewsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val newsUseCases: NewsUseCases
) : ViewModel() {

    var articles by mutableStateOf<List<Article>>(emptyList())
        private set

    init {
        newsUseCases.getHistory()
            .onEach { articles = it }
            .launchIn(viewModelScope)
    }

    fun clearHistory() {
        viewModelScope.launch { newsUseCases.clearHistory() }
    }
}
