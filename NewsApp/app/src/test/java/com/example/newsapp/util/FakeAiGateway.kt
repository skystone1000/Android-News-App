package com.example.newsapp.util

import com.example.newsapp.domain.ai.AiGateway
import com.example.newsapp.domain.ai.ArticleInsight
import com.example.newsapp.domain.model.Article

/** Fake [AiGateway] returning a canned result for tests. */
class FakeAiGateway(
    private val result: Result<ArticleInsight> =
        Result.success(ArticleInsight(summary = "summary", sentiment = "neutral", tags = emptyList()))
) : AiGateway {
    override suspend fun summarize(article: Article): Result<ArticleInsight> = result
}
