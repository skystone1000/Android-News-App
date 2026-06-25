package com.skystone1000.briefly.data.ai

import com.skystone1000.briefly.data.ai.dto.ClaudeMessage
import com.skystone1000.briefly.data.ai.dto.ClaudeRequest
import com.skystone1000.briefly.data.local.AiInsightDao
import com.skystone1000.briefly.data.local.AiInsightEntity
import com.skystone1000.briefly.domain.ai.AiGateway
import com.skystone1000.briefly.domain.ai.ArticleInsight
import com.skystone1000.briefly.domain.model.Article
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/**
 * Claude-backed [AiGateway]. Results are cached in Room by article URL to avoid repeat
 * cost/latency. The key comes from [apiKey] (BuildConfig in dev; a proxy strips the need
 * for an embedded key in production — see docs/ROADMAP.md Phase 5).
 */
class ClaudeAiGateway(
    private val service: ClaudeService,
    private val apiKey: String,
    private val dao: AiInsightDao,
    private val gson: Gson = Gson()
) : AiGateway {

    // Guard clauses + a catch-all that map every failure path to Result.failure.
    @Suppress("TooGenericExceptionCaught", "ReturnCount")
    override suspend fun summarize(article: Article): Result<ArticleInsight> {
        dao.get(article.url)?.let { return Result.success(it.toInsight()) }

        if (apiKey.isBlank()) {
            return Result.failure(IllegalStateException("Claude API key not configured"))
        }

        return try {
            val response = service.createMessage(
                apiKey = apiKey,
                request = ClaudeRequest(
                    model = MODEL,
                    maxTokens = MAX_TOKENS,
                    messages = listOf(ClaudeMessage(role = "user", content = buildPrompt(article)))
                )
            )

            if (response.stopReason == "refusal") {
                return Result.failure(IllegalStateException("Request was declined by safety classifiers"))
            }

            val text = response.content
                ?.firstOrNull { it.type == "text" }
                ?.text
                ?: return Result.failure(IllegalStateException("Empty AI response"))

            val insight = parseInsight(text)
            dao.upsert(insight.toEntity(article.url))
            Result.success(insight)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildPrompt(article: Article): String = """
        Summarize the following news article in 2-3 sentences, classify its overall sentiment
        as exactly one of: positive, neutral, negative, and extract up to 4 short topic tags.
        Respond with ONLY a JSON object, no markdown, in this shape:
        {"summary": "...", "sentiment": "neutral", "tags": ["...", "..."]}

        Title: ${article.title}
        Description: ${article.description}
        Content: ${article.content}
    """.trimIndent()

    private fun parseInsight(text: String): ArticleInsight {
        val json = text.substringAfter('{', "").let { "{$it" }
            .substringBeforeLast('}', "").let { "$it}" }
        val dto = gson.fromJson(json.ifBlank { text }, InsightJson::class.java)
        return ArticleInsight(
            summary = dto.summary.orEmpty().ifBlank { "No summary available." },
            sentiment = dto.sentiment.orEmpty().ifBlank { "neutral" },
            tags = dto.tags.orEmpty()
        )
    }

    private data class InsightJson(
        @SerializedName("summary") val summary: String?,
        @SerializedName("sentiment") val sentiment: String?,
        @SerializedName("tags") val tags: List<String>?
    )

    private fun AiInsightEntity.toInsight() = ArticleInsight(
        summary = summary,
        sentiment = sentiment,
        tags = tags.split('\n').filter { it.isNotBlank() }
    )

    private fun ArticleInsight.toEntity(url: String) = AiInsightEntity(
        articleUrl = url,
        summary = summary,
        sentiment = sentiment,
        tags = tags.joinToString("\n")
    )

    private companion object {
        // Default per docs/ROADMAP.md Phase 5: Haiku for cost. Swap to claude-sonnet-4-6
        // (or claude-opus-4-8) for higher quality.
        const val MODEL = "claude-haiku-4-5"
        const val MAX_TOKENS = 400
    }
}
