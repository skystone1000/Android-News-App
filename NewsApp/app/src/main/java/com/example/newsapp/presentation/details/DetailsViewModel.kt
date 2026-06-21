package com.example.newsapp.presentation.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.domain.ai.AiGateway
import com.example.newsapp.domain.manager.SettingsManager
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.usecases.news.NewsUseCases
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
        sideEffect = "Article Saved"
    }

    private suspend fun deleteArticle(article: Article) {
        newsUseCases.deleteArticle(article)
        sideEffect = "Article Deleted"
    }
}
