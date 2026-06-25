package com.skystone1000.briefly.data.remote.dto

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/** Mapper tests for the providers added in Phase C (NewsData.io, Currents, Mediastack). */
class NewProviderMappersTest {

    // --- NewsData.io ---

    @Test
    fun `newsdata maps link to url and first creator to author`() {
        val dto = NewsDataArticleDto(
            title = "Headline",
            link = "https://example.com/a",
            description = "Desc",
            content = "Body",
            imageUrl = "https://example.com/a.png",
            pubDate = "2026-06-22",
            sourceId = "bbc",
            sourceName = "BBC",
            creator = listOf("Jane", "John")
        )
        val article = dto.toArticleOrNull()!!
        assertThat(article.url).isEqualTo("https://example.com/a")
        assertThat(article.author).isEqualTo("Jane")
        assertThat(article.source.name).isEqualTo("BBC")
    }

    @Test
    fun `newsdata drops an article with no link`() {
        val dto = NewsDataArticleDto(
            title = "T", link = null, description = null, content = null,
            imageUrl = null, pubDate = null, sourceId = null, sourceName = null, creator = null
        )
        assertThat(dto.toArticleOrNull()).isNull()
    }

    // --- Currents ---

    @Test
    fun `currents maps a complete dto and labels the source`() {
        val dto = CurrentsArticleDto(
            id = "1", title = "Headline", description = "Desc",
            url = "https://example.com/a", author = "Jane",
            image = "https://example.com/a.png", published = "2026-06-22"
        )
        val article = dto.toArticleOrNull()!!
        assertThat(article.title).isEqualTo("Headline")
        assertThat(article.source.name).isEqualTo("Currents")
        assertThat(article.urlToImage).isEqualTo("https://example.com/a.png")
    }

    @Test
    fun `currents ignores a non-http image placeholder`() {
        val dto = CurrentsArticleDto(
            id = "1", title = "Headline", description = "Desc",
            url = "https://example.com/a", author = null,
            image = "None", published = null
        )
        assertThat(dto.toArticleOrNull()!!.urlToImage).isEmpty()
    }

    // --- Mediastack ---

    @Test
    fun `mediastack maps a complete dto`() {
        val dto = MediastackArticleDto(
            author = "Jane", title = "Headline", description = "Desc",
            url = "https://example.com/a", source = "BBC",
            image = "https://example.com/a.png", category = "technology",
            publishedAt = "2026-06-22T00:00:00+00:00"
        )
        val article = dto.toArticleOrNull()!!
        assertThat(article.url).isEqualTo("https://example.com/a")
        assertThat(article.source.name).isEqualTo("BBC")
        assertThat(article.author).isEqualTo("Jane")
    }

    @Test
    fun `mediastack drops an article with no title`() {
        val dto = MediastackArticleDto(
            author = null, title = null, description = null,
            url = "https://example.com/a", source = null,
            image = null, category = null, publishedAt = null
        )
        assertThat(dto.toArticleOrNull()).isNull()
    }
}
