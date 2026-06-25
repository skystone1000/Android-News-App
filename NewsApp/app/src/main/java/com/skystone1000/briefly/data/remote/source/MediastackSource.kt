package com.skystone1000.briefly.data.remote.source

import com.skystone1000.briefly.data.remote.api.MediastackService
import com.skystone1000.briefly.data.remote.dto.toArticleOrNull
import com.skystone1000.briefly.domain.security.ApiKeyStore

/**
 * mediastack.com source. Offset pagination — the cursor encodes the numeric offset; the next
 * cursor is `offset + limit` until a page comes back empty. Its categories match the shared set.
 */
class MediastackSource(
    private val service: MediastackService,
    private val apiKeyStore: ApiKeyStore
) : NewsSource {

    override val id: String = ID

    override suspend fun getNews(category: String?, cursor: String?, pageSize: Int): NewsPage {
        val offset = offsetOf(cursor)
        val articles = service.getNews(
            accessKey = apiKey(),
            categories = category?.takeIf { it.isNotBlank() },
            offset = offset,
            limit = pageSize
        ).data.orEmpty().mapNotNull { it.toArticleOrNull() }
        return NewsPage(articles, nextOffsetCursor(offset, pageSize, articles))
    }

    override suspend fun searchNews(query: String, cursor: String?, pageSize: Int): NewsPage {
        val offset = offsetOf(cursor)
        val articles = service.searchNews(
            accessKey = apiKey(),
            keywords = query,
            offset = offset,
            limit = pageSize
        ).data.orEmpty().mapNotNull { it.toArticleOrNull() }
        return NewsPage(articles, nextOffsetCursor(offset, pageSize, articles))
    }

    private fun offsetOf(cursor: String?): Int = cursor?.toIntOrNull() ?: 0

    private fun nextOffsetCursor(offset: Int, pageSize: Int, loaded: List<*>): String? =
        if (loaded.isEmpty()) null else (offset + pageSize).toString()

    private suspend fun apiKey(): String =
        apiKeyStore.getKey(ID).ifBlank { throw MissingApiKeyException(ID) }

    companion object {
        const val ID = "mediastack"
    }
}
