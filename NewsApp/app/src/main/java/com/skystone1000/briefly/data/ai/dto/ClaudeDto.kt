package com.skystone1000.briefly.data.ai.dto

import com.google.gson.annotations.SerializedName

/** Request body for Anthropic's Messages API (`POST /v1/messages`). */
data class ClaudeRequest(
    val model: String,
    @SerializedName("max_tokens") val maxTokens: Int,
    val messages: List<ClaudeMessage>
)

data class ClaudeMessage(
    val role: String,
    val content: String
)

data class ClaudeResponse(
    val content: List<ClaudeContentBlock>?,
    @SerializedName("stop_reason") val stopReason: String?
)

data class ClaudeContentBlock(
    val type: String?,
    val text: String?
)
