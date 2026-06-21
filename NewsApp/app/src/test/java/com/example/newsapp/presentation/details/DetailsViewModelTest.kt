package com.example.newsapp.presentation.details

import com.example.newsapp.domain.usecases.news.DeleteArticle
import com.example.newsapp.domain.usecases.news.GetNews
import com.example.newsapp.domain.usecases.news.NewsUseCases
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
class DetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun useCases(repository: FakeNewsRepository) = NewsUseCases(
        getNews = GetNews(repository),
        searchNews = SearchNews(repository),
        upsertArticle = UpsertArticle(repository),
        deleteArticle = DeleteArticle(repository),
        selectArticles = SelectArticles(repository),
        selectArticle = SelectArticle(repository)
    )

    @Test
    fun `bookmarking a new article saves it and emits Saved`() = runTest {
        val repository = FakeNewsRepository()
        val viewModel = DetailsViewModel(useCases(repository))
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
        val viewModel = DetailsViewModel(useCases(repository))

        viewModel.onEvent(DetailsEvent.UpsertDeleteArticle(article))

        assertThat(repository.bookmarks).isEmpty()
        assertThat(viewModel.sideEffect).isEqualTo("Article Deleted")
    }

    @Test
    fun `RemoveSideEffect clears the message`() = runTest {
        val repository = FakeNewsRepository()
        val viewModel = DetailsViewModel(useCases(repository))
        viewModel.onEvent(DetailsEvent.UpsertDeleteArticle(testArticle()))

        viewModel.onEvent(DetailsEvent.RemoveSideEffect)

        assertThat(viewModel.sideEffect).isNull()
    }
}
