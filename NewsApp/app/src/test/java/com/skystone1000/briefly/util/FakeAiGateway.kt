package com.skystone1000.briefly.util

import com.skystone1000.briefly.domain.ai.AiGateway
import com.skystone1000.briefly.domain.ai.ArticleInsight
import com.skystone1000.briefly.domain.model.Article

/** Fake [AiGateway] returning a canned result for tests. */
class FakeAiGateway(
    private val result: Result<ArticleInsight> =
        Result.success(ArticleInsight(summary = "summary", sentiment = "neutral", tags = emptyList()))
) : AiGateway {
    override suspend fun summarize(article: Article): Result<ArticleInsight> = result
}
