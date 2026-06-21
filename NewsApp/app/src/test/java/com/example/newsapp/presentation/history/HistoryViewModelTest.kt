package com.example.newsapp.presentation.history

import com.example.newsapp.domain.usecases.news.ClearHistory
import com.example.newsapp.domain.usecases.news.DeleteArticle
import com.example.newsapp.domain.usecases.news.GetHistory
import com.example.newsapp.domain.usecases.news.GetNews
import com.example.newsapp.domain.usecases.news.NewsUseCases
import com.example.newsapp.domain.usecases.news.RecordHistory
import com.example.newsapp.domain.usecases.news.SearchNews
import com.example.newsapp.domain.usecases.news.SelectArticle
import com.example.newsapp.domain.usecases.news.SelectArticles
import com.example.newsapp.domain.usecases.news.UpsertArticle
import com.example.newsapp.util.FakeNewsRepository
import com.example.newsapp.util.MainDispatcherRule
import com.example.newsapp.util.testArticle
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
