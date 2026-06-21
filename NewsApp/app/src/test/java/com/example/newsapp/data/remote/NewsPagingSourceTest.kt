package com.example.newsapp.data.remote

import androidx.paging.PagingSource
import com.example.newsapp.data.remote.source.NewsSource
import com.example.newsapp.domain.model.Article
import com.example.newsapp.domain.model.Source
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Test

private fun article(url: String) = Article(
    source = Source("id", "name"),
    author = "a", title = "t", description = "d",
    url = url, urlToImage = "", publishedAt = "", content = ""
)

private class FakeNewsSource(private val pages: Map<Int, List<Article>>) : NewsSource {
    override val id = "fake"
    override suspend fun getNews(category: String?, page: Int, pageSize: Int) = pages[page] ?: emptyList()
    override suspend fun searchNews(query: String, page: Int, pageSize: Int) = pages[page] ?: emptyList()
}

class NewsPagingSourceTest {

    @Test
    fun `first load returns data with a next key`() = runTest {
        val source = FakeNewsSource(mapOf(1 to listOf(article("https://a"), article("https://b"))))
        val paging = NewsPagingSource(source, category = null, query = null)

        val result = paging.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
        )

        assertThat(result).isInstanceOf(PagingSource.LoadResult.Page::class.java)
        val page = result as PagingSource.LoadResult.Page
        assertThat(page.data).hasSize(2)
        assertThat(page.prevKey).isNull()
        assertThat(page.nextKey).isEqualTo(2)
    }

    @Test
    fun `empty page terminates paging with a null next key`() = runTest {
        val source = FakeNewsSource(emptyMap())
        val paging = NewsPagingSource(source, category = null, query = null)

        val result = paging.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
        )

        val page = result as PagingSource.LoadResult.Page
        assertThat(page.data).isEmpty()
        assertThat(page.nextKey).isNull()
    }
}
