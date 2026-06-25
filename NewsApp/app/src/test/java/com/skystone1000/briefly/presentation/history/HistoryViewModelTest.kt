package com.skystone1000.briefly.presentation.history

import com.skystone1000.briefly.domain.usecases.news.ClearHistory
import com.skystone1000.briefly.domain.usecases.news.DeleteArticle
import com.skystone1000.briefly.domain.usecases.news.GetHistory
import com.skystone1000.briefly.domain.usecases.news.GetNews
import com.skystone1000.briefly.domain.usecases.news.NewsUseCases
import com.skystone1000.briefly.domain.usecases.news.RecordHistory
import com.skystone1000.briefly.domain.usecases.news.SearchNews
import com.skystone1000.briefly.domain.usecases.news.SelectArticle
import com.skystone1000.briefly.domain.usecases.news.SelectArticles
import com.skystone1000.briefly.domain.usecases.news.UpsertArticle
import com.skystone1000.briefly.util.FakeNewsRepository
import com.skystone1000.briefly.util.MainDispatcherRule
import com.skystone1000.briefly.util.testArticle
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun useCases(repository: FakeNewsRepository) = NewsUseCases(
        getNews = GetNews(repository),
        searchNews = SearchNews(repository),
        upsertArticle = UpsertArticle(repository),
        deleteArticle = DeleteArticle(repository),
        selectArticles = SelectArticles(repository),
        selectArticle = SelectArticle(repository),
        recordHistory = RecordHistory(repository),
        getHistory = GetHistory(repository),
        clearHistory = ClearHistory(repository)
    )

    @Test
    fun `recorded articles surface in the history list`() = runTest {
        val repository = FakeNewsRepository()
        repository.recordHistory(testArticle("https://example.com/1"))
        val viewModel = HistoryViewModel(useCases(repository))

        assertThat(viewModel.articles).hasSize(1)
        assertThat(viewModel.articles.first().url).isEqualTo("https://example.com/1")
    }

    @Test
    fun `clearHistory empties the list`() = runTest {
        val repository = FakeNewsRepository()
        repository.recordHistory(testArticle("https://example.com/1"))
        val viewModel = HistoryViewModel(useCases(repository))

        viewModel.clearHistory()

        assertThat(viewModel.articles).isEmpty()
    }
}
