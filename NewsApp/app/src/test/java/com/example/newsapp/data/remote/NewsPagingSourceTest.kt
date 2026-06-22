package com.example.newsapp.data.remote

import androidx.paging.PagingSource
import com.example.newsapp.data.remote.source.MissingApiKeyException
import com.example.newsapp.data.remote.source.NewsPage
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

private class FakeNewsSource(private val pages: Map<String?, NewsPage>) : NewsSource {
    override val id = "fake"
    override suspend fun getNews(category: String?, cursor: String?, pageSize: Int) =
        pages[cursor] ?: NewsPage(emptyList(), null)
    override suspend fun searchNews(query: String, cursor: String?, pageSize: Int) =
        pages[cursor] ?: NewsPage(emptyList(), null)
}

private class ThrowingNewsSource : NewsSource {
    override val id = "fake"
    override suspend fun getNews(category: String?, cursor: String?, pageSize: Int): NewsPage =
        throw MissingApiKeyException(id)
    override suspend fun searchNews(query: String, cursor: String?, pageSize: Int): NewsPage =
        throw MissingApiKeyException(id)
}

class NewsPagingSourceTest {

    @Test
    fun `first load returns data with a next cursor`() = runTest {
        val source = FakeNewsSource(
            mapOf(null to NewsPage(listOf(article("https://a"), article("https://b")), nextCursor = "2"))
        )
        val paging = NewsPagingSource(source, category = null, query = null)

        val result = paging.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
        )

        assertThat(result).isInstanceOf(PagingSource.LoadResult.Page::class.java)
        val page = result as PagingSource.LoadResult.Page
        assertThat(page.data).hasSize(2)
        assertThat(page.prevKey).isNull()
        assertThat(page.nextKey).isEqualTo("2")
    }

    @Test
    fun `empty page terminates paging with a null next cursor`() = runTest {
        val source = FakeNewsSource(emptyMap())
        val paging = NewsPagingSource(source, category = null, query = null)

        val result = paging.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
        )

        val page = result as PagingSource.LoadResult.Page
        assertThat(page.data).isEmpty()
        assertThat(page.nextKey).isNull()
    }

    @Test
    fun `a missing api key surfaces as a load error`() = runTest {
        val paging = NewsPagingSource(ThrowingNewsSource(), category = null, query = null)

        val result = paging.load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = false)
        )

        assertThat(result).isInstanceOf(PagingSource.LoadResult.Error::class.java)
        val error = result as PagingSource.LoadResult.Error
        assertThat(error.throwable).isInstanceOf(MissingApiKeyException::class.java)
    }
}
