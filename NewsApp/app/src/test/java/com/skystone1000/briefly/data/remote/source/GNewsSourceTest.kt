package com.skystone1000.briefly.data.remote.source

import com.skystone1000.briefly.data.remote.api.GNewsService
import com.skystone1000.briefly.data.remote.dto.GNewsArticleDto
import com.skystone1000.briefly.data.remote.dto.GNewsResponse
import com.skystone1000.briefly.data.remote.dto.GNewsSourceDto
import com.skystone1000.briefly.domain.security.ApiKeyStore
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

private fun gdto(url: String) = GNewsArticleDto(
    title = "t", description = "d", content = "c", url = url,
    image = "", publishedAt = "", source = GNewsSourceDto(name = "s", url = "")
)

/** Records the `max` GNews was called with so the test can assert it stays within the free limit. */
private class RecordingGNewsService(private val response: GNewsResponse) : GNewsService {
    var headlinesMax: Int? = null
    var searchMax: Int? = null

    override suspend fun getTopHeadlines(
        category: String?, page: Int, max: Int, apiKey: String, lang: String
    ): GNewsResponse {
        headlinesMax = max
        return response
    }

    override suspend fun searchNews(
        query: String, page: Int, max: Int, apiKey: String, lang: String
    ): GNewsResponse {
        searchMax = max
        return response
    }
}

private class FakeGNewsKeyStore(private val key: String) : ApiKeyStore {
    override fun keys(): Flow<Map<String, String>> = flowOf(mapOf(GNewsSource.ID to key))
    override suspend fun getKey(sourceId: String): String = key
    override suspend fun setKey(sourceId: String, key: String) = Unit
    override suspend fun clearKey(sourceId: String) = Unit
}

class GNewsSourceTest {

    @Test
    fun `getNews clamps max to the free-plan limit`() = runTest {
        val service = RecordingGNewsService(GNewsResponse(totalArticles = 1, articles = listOf(gdto("https://a"))))
        val source = GNewsSource(service, FakeGNewsKeyStore("key"))

        source.getNews(category = "general", cursor = null, pageSize = 20)

        // GNews free plan rejects max > 10, so the source must cap the request.
        assertThat(service.headlinesMax).isAtMost(10)
    }

    @Test
    fun `searchNews clamps max to the free-plan limit`() = runTest {
        val service = RecordingGNewsService(GNewsResponse(totalArticles = 1, articles = listOf(gdto("https://a"))))
        val source = GNewsSource(service, FakeGNewsKeyStore("key"))

        source.searchNews(query = "bitcoin", cursor = null, pageSize = 20)

        assertThat(service.searchMax).isAtMost(10)
    }
}
