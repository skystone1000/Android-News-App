package com.example.newsapp.data.remote.dto

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NewsApiMapperTest {

    @Test
    fun `maps a complete dto to a domain article`() {
        val dto = NewsApiArticleDto(
            source = NewsApiSourceDto(id = "bbc", name = "BBC"),
            author = "Jane",
            title = "Headline",
            description = "Desc",
            url = "https://example.com/a",
            urlToImage = "https://example.com/a.png",
            publishedAt = "2026-06-21",
            content = "Body"
        )

        val article = dto.toArticleOrNull()

        assertThat(article).isNotNull()
        assertThat(article!!.title).isEqualTo("Headline")
        assertThat(article.source.name).isEqualTo("BBC")
        assertThat(article.url).isEqualTo("https://example.com/a")
    }

    @Test
    fun `drops articles missing a url`() {
        val dto = NewsApiArticleDto(
            source = null, author = null, title = "Headline", description = null,
            url = null, urlToImage = null, publishedAt = null, content = null
        )
        assertThat(dto.toArticleOrNull()).isNull()
    }

    @Test
    fun `null fields default to empty strings`() {
        val dto = NewsApiArticleDto(
            source = null, author = null, title = "T", description = null,
            url = "https://x.com", urlToImage = null, publishedAt = null, content = null
        )
        val article = dto.toArticleOrNull()!!
        assertThat(article.author).isEmpty()
        assertThat(article.description).isEmpty()
        assertThat(article.source.name).isEmpty()
    }
}
