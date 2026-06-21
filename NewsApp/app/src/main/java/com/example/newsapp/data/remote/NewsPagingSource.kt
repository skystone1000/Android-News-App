package com.example.newsapp.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.newsapp.data.remote.source.NewsSource
import com.example.newsapp.domain.model.Article

/**
 * Pages articles from a [NewsSource]. If [query] is non-blank it searches; otherwise it
 * loads top headlines for [category]. De-duplicates by URL across pages.
 */
class NewsPagingSource(
    private val newsSource: NewsSource,
    private val category: String?,
    private val query: String?
) : PagingSource<Int, Article>() {

    private val seenUrls = mutableSetOf<String>()

    @Suppress("TooGenericExceptionCaught") // any load failure maps to LoadResult.Error
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val page = params.key ?: STARTING_PAGE
        return try {
            val articles = if (query.isNullOrBlank()) {
                newsSource.getNews(category, page, params.loadSize)
            } else {
                newsSource.searchNews(query, page, params.loadSize)
            }
            val deduped = articles.filter { seenUrls.add(it.url) }
            LoadResult.Page(
                data = deduped,
                prevKey = if (page == STARTING_PAGE) null else page - 1,
                nextKey = if (articles.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchor ->
            val closest = state.closestPageToPosition(anchor)
            closest?.prevKey?.plus(1) ?: closest?.nextKey?.minus(1)
        }
    }

    private companion object {
        const val STARTING_PAGE = 1
    }
}
