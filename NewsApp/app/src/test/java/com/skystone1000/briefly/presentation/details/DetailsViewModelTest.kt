package com.skystone1000.briefly.presentation.details

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
import com.skystone1000.briefly.util.FakeAiGateway
import com.skystone1000.briefly.util.FakeNewsRepository
import com.skystone1000.briefly.util.FakeSettingsManager
import com.skystone1000.briefly.util.MainDispatcherRule
import com.skystone1000.briefly.util.testArticle
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {

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
    fun `bookmarking a new article saves it and emits Saved`() = runTest {
        val repository = FakeNewsRepository()
        val viewModel = DetailsViewModel(useCases(repository), FakeAiGateway(), FakeSettingsManager())
        val article = testArticle()

        viewModel.onEvent(DetailsEvent.UpsertDeleteArticle(article))

        assertThat(repository.bookmarks).contains(article)
        assertThat(viewModel.sideEffect).isEqualTo("Article Saved")
    }

    @Test
    fun `bookmarking an already-saved article deletes it and emits Deleted`() = runTest {
        val repository = FakeNewsRepository()
        val article = testArticle()
        repository.upsertArticle(article)
        val viewModel = DetailsViewModel(useCases(repository), FakeAiGateway(), FakeSettingsManager())

        viewModel.onEvent(DetailsEvent.UpsertDeleteArticle(article))

        assertThat(repository.bookmarks).isEmpty()
        assertThat(viewModel.sideEffect).isEqualTo("Article Deleted")
    }

    @Test
    fun `RemoveSideEffect clears the message`() = runTest {
        val repository = FakeNewsRepository()
        val viewModel = DetailsViewModel(useCases(repository), FakeAiGateway(), FakeSettingsManager())
        viewModel.onEvent(DetailsEvent.UpsertDeleteArticle(testArticle()))

        viewModel.onEvent(DetailsEvent.RemoveSideEffect)

        assertThat(viewModel.sideEffect).isNull()
    }
}
