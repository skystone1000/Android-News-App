package com.example.newsapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.newsapp.domain.manager.SettingsManager
import com.example.newsapp.domain.model.UserSettings
import com.example.newsapp.domain.usecases.news.NewsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    newsUseCases: NewsUseCases
) : ViewModel() {

    val settings: StateFlow<UserSettings> = settingsManager.settings().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = UserSettings()
    )

    /** Rebuilds the paged feed whenever the data source or selected category changes. */
    val news = settingsManager.settings()
        .map { it.dataSourceId to it.selectedCategory }
        .distinctUntilChanged()
        .flatMapLatest { (_, category) -> newsUseCases.getNews(category) }
        .cachedIn(viewModelScope)

    fun selectCategory(category: String) {
        viewModelScope.launch { settingsManager.setSelectedCategory(category) }
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5000L
    }
}
