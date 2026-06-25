package com.skystone1000.briefly.presentation.details

import com.skystone1000.briefly.domain.ai.ArticleInsight

/** UI state for the AI insight card on the detail screen. */
sealed interface AiInsightState {
    data object Idle : AiInsightState
    data object Loading : AiInsightState
    data class Success(val insight: ArticleInsight) : AiInsightState
    data class Error(val message: String) : AiInsightState
}
