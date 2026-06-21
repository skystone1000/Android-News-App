package com.example.newsapp.presentation.settings

import com.example.newsapp.domain.model.ThemeMode
import com.example.newsapp.util.FakeSettingsManager
import com.example.newsapp.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `toggling a followed category adds it then removes it`() = runTest {
        val manager = FakeSettingsManager()
        val viewModel = SettingsViewModel(manager)
        backgroundScope.launch { viewModel.settings.collect {} } // keep WhileSubscribed active

        viewModel.onEvent(SettingsEvent.ToggleFollowedCategory("business"))
        assertThat(manager.state.value.followedCategories).contains("business")

        viewModel.onEvent(SettingsEvent.ToggleFollowedCategory("business"))
        assertThat(manager.state.value.followedCategories).doesNotContain("business")
    }

    @Test
    fun `setting theme mode persists it`() = runTest {
        val manager = FakeSettingsManager()
        val viewModel = SettingsViewModel(manager)

        viewModel.onEvent(SettingsEvent.SetThemeMode(ThemeMode.DARK))

        assertThat(manager.state.value.themeMode).isEqualTo(ThemeMode.DARK)
    }

    @Test
    fun `changing data source persists it`() = runTest {
        val manager = FakeSettingsManager()
        val viewModel = SettingsViewModel(manager)

        viewModel.onEvent(SettingsEvent.SetDataSource("gnews"))

        assertThat(manager.state.value.dataSourceId).isEqualTo("gnews")
    }
}
