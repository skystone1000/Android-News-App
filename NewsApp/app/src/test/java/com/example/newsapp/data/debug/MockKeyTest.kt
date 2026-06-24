package com.example.newsapp.data.debug

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MockKeyTest {

    @Test
    fun `key is sourceId endpoint and sorted param slug`() {
        val key = mockKey(
            sourceId = "newsapi",
            pathSegments = listOf("v2", "top-headlines"),
            queryParams = listOf("page" to "1", "category" to "business"),
        )
        assertThat(key).isEqualTo("newsapi/top-headlines/category=business&page=1")
    }

    @Test
    fun `credential params are excluded from the key`() {
        val key = mockKey(
            sourceId = "gnews",
            pathSegments = listOf("api", "v4", "top-headlines"),
            queryParams = listOf("category" to "sports", "apiKey" to "SECRET", "token" to "T"),
        )
        assertThat(key).isEqualTo("gnews/top-headlines/category=sports")
        assertThat(key).doesNotContain("SECRET")
        assertThat(key).doesNotContain("T")
    }

    @Test
    fun `no params yields a default slug`() {
        val key = mockKey("currents", listOf("v1", "latest-news"), emptyList())
        assertThat(key).isEqualTo("currents/latest-news/default")
    }

    @Test
    fun `unsafe characters are sanitized`() {
        val key = mockKey("newsapi", listOf("v2", "everything"), listOf("q" to "hello world"))
        assertThat(key).isEqualTo("newsapi/everything/q=hello_world")
    }

    @Test
    fun `trailing empty path segment is ignored for the endpoint`() {
        val key = mockKey("mediastack", listOf("v1", "news", ""), emptyList())
        assertThat(key).isEqualTo("mediastack/news/default")
    }
}
