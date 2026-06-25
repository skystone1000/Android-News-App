package com.skystone1000.briefly.presentation.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skystone1000.briefly.domain.ai.AiGateway
import com.skystone1000.briefly.domain.manager.SettingsManager
import com.skystone1000.briefly.domain.model.Article
import com.skystone1000.briefly.domain.usecases.news.NewsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val newsUseCases: NewsUseCases,
    private val aiGateway: AiGateway,
    private val settingsManager: SettingsManager
) : ViewModel() {

    /** One-shot user message (e.g. "Article Saved"); cleared via [DetailsEvent.RemoveSideEffect]. */
    var sideEffect by mutableStateOf<String?>(null)
        private set

    var aiState by mutableStateOf<AiInsightState>(AiInsightState.Idle)
        private set

    /** Whether the current article is currently bookmarked (drives the filled/outline icon). */
    var isBookmarked by mutableStateOf(false)
        private set

    fun onEvent(event: DetailsEvent) {
        when (event) {
            is DetailsEvent.UpsertDeleteArticle -> viewModelScope.launch {
                val existing = newsUseCases.selectArticle(event.article.url)
                if (existing == null) {
                    upsertArticle(event.article)
                } else {
                    deleteArticle(event.article)
                }
            }

            is DetailsEvent.RemoveSideEffect -> sideEffect = null
        }
    }

    /** Records that the user opened [article] so it appears in reading history. */
    fun recordHistory(article: Article) {
        viewModelScope.launch { newsUseCases.recordHistory(article) }
    }

    /** Refreshes [isBookmarked] for [article] from the bookmark store. */
    fun syncBookmark(article: Article) {
        viewModelScope.launch { isBookmarked = newsUseCases.selectArticle(article.url) != null }
    }

    /** Loads an AI insight only when the user has enabled AI summaries in Settings. */
    fun loadInsightIfEnabled(article: Article) {
        viewModelScope.launch {
            if (!settingsManager.settings().first().aiSummariesEnabled) {
                aiState = AiInsightState.Idle
                return@launch
            }
            aiState = AiInsightState.Loading
            aiGateway.summarize(article)
                .onSuccess { aiState = AiInsightState.Success(it) }
                .onFailure { aiState = AiInsightState.Error(it.message ?: "AI unavailable") }
        }
    }

    private suspend fun upsertArticle(article: Article) {
        newsUseCases.upsertArticle(article)
        isBookmarked = true
        sideEffect = "Article Saved"
    }

    private suspend fun deleteArticle(article: Article) {
        newsUseCases.deleteArticle(article)
        isBookmarked = false
        sideEffect = "Article Deleted"
    }
}
