package com.example.newsapp.domain.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SourceCatalogTest {

    @Test
    fun `catalog lists the five supported providers with unique ids`() {
        val ids = SourceCatalog.ALL_SOURCES.map { it.id }
        assertThat(ids).containsExactly("newsapi", "newsdata", "gnews", "currents", "mediastack")
        assertThat(ids).containsNoDuplicates()
    }

    @Test
    fun `every provider has display metadata and a positive quota`() {
        SourceCatalog.ALL_SOURCES.forEach { source ->
            assertThat(source.displayName).isNotEmpty()
            assertThat(source.signupUrl).startsWith("https://")
            assertThat(source.keyParamHint).isNotEmpty()
            assertThat(source.quota.limit).isGreaterThan(0)
        }
    }

    @Test
    fun `byId resolves a known provider and returns null otherwise`() {
        assertThat(SourceCatalog.byId("gnews")?.displayName).isEqualTo("GNews")
        assertThat(SourceCatalog.byId("nope")).isNull()
    }
}
