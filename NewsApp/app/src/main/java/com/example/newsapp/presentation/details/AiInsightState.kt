package com.example.newsapp.presentation.details

import com.example.newsapp.domain.ai.ArticleInsight

/** UI state for the AI insight card on the detail screen. */
sealed interface AiInsightState {
    data object Idle : AiInsightState
    data object Loading : AiInsightState
    data class Success(val insight: ArticleInsight) : AiInsightState
    data class Error(val message: String) : AiInsightState
}
