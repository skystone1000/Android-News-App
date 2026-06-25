package com.skystone1000.briefly.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.skystone1000.briefly.data.remote.source.NewsSource
import com.skystone1000.briefly.domain.model.Article

/**
 * Pages articles from a [NewsSource] using the source's opaque string cursor. If [query] is
 * non-blank it searches; otherwise it loads top headlines for [category]. De-duplicates by URL
 * across pages. Cursor paging is forward-only (no prepend).
 */
class NewsPagingSource(
    private val newsSource: NewsSource,
    private val category: String?,
    private val query: String?
) : PagingSource<String, Article>() {

    private val seenUrls = mutableSetOf<String>()

    @Suppress("TooGenericExceptionCaught") // any load failure maps to LoadResult.Error
    override suspend fun load(params: LoadParams<String>): LoadResult<String, Article> {
        val cursor = params.key
        return try {
            val page = if (query.isNullOrBlank()) {
                newsSource.getNews(category, cursor, params.loadSize)
            } else {
                newsSource.searchNews(query, cursor, params.loadSize)
            }
            val deduped = page.articles.filter { seenUrls.add(it.url) }
            LoadResult.Page(
                data = deduped,
                prevKey = null,
                nextKey = page.nextCursor
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<String, Article>): String? = null
}
