package com.example.newsapp.data.remote.source

import com.example.newsapp.data.remote.api.CurrentsService
import com.example.newsapp.data.remote.dto.CurrentsArticleDto
import com.example.newsapp.data.remote.dto.CurrentsResponse
import com.example.newsapp.domain.security.ApiKeyStore
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

private fun dto(url: String) = CurrentsArticleDto(
    id = "id", title = "t", description = "d", url = url,
    author = "a", image = "", published = ""
)

/** Records the query params Currents was called with so the test can assert the request shape. */
private class RecordingCurrentsService(
    private val response: CurrentsResponse
) : CurrentsService {
    var latestLanguage: String? = null
    var searchKeywords: String? = null
    var searchLanguage: String? = null

    override suspend fun getLatest(apiKey: String, language: String): CurrentsResponse {
        latestLanguage = language
        return response
    }

    override suspend fun searchNews(apiKey: String, keywords: String, language: String): CurrentsResponse {
        searchKeywords = keywords
        searchLanguage = language
        return response
    }
}

private class FakeApiKeyStore(private val key: String) : ApiKeyStore {
    override fun keys(): Flow<Map<String, String>> = flowOf(mapOf(CurrentsSource.ID to key))
    override suspend fun getKey(sourceId: String): String = key
    override suspend fun setKey(sourceId: String, key: String) = Unit
    override suspend fun clearKey(sourceId: String) = Unit
}

class CurrentsSourceTest {

    @Test
    fun `getNews maps articles and returns a single page`() = runTest {
        val service = RecordingCurrentsService(
            CurrentsResponse(status = "ok", news = listOf(dto("https://a"), dto("https://b")))
        )
        val source = CurrentsSource(service, FakeApiKeyStore("key"))

        val page = source.getNews(category = "business", cursor = null, pageSize = 20)

        assertThat(page.articles).hasSize(2)
        // latest-news does not paginate (it accepts only `language`), so there is no next page.
        assertThat(page.nextCursor).isNull()
        assertThat(service.latestLanguage).isEqualTo("en")
    }

    @Test
    fun `searchNews maps articles and returns a single page`() = runTest {
        val service = RecordingCurrentsService(
            CurrentsResponse(status = "ok", news = listOf(dto("https://a")))
        )
        val source = CurrentsSource(service, FakeApiKeyStore("key"))

        val page = source.searchNews(query = "bitcoin", cursor = null, pageSize = 20)

        assertThat(page.articles).hasSize(1)
        assertThat(page.nextCursor).isNull()
        assertThat(service.searchKeywords).isEqualTo("bitcoin")
    }

    @Test
    fun `getNews throws when no key is configured`() = runTest {
        val service = RecordingCurrentsService(CurrentsResponse(status = "ok", news = emptyList()))
        val source = CurrentsSource(service, FakeApiKeyStore(""))

        try {
            source.getNews(category = null, cursor = null, pageSize = 20)
            error("expected MissingApiKeyException")
        } catch (e: MissingApiKeyException) {
            assertThat(e).isInstanceOf(MissingApiKeyException::class.java)
        }
    }
}
