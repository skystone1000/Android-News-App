package com.example.newsapp.presentation.settings

import com.example.newsapp.domain.model.ThemeMode
import com.example.newsapp.util.FakeApiKeyStore
import com.example.newsapp.util.FakeApiUsageStore
import com.example.newsapp.util.FakeSettingsManager
import com.example.newsapp.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun viewModel(
        manager: FakeSettingsManager = FakeSettingsManager(),
        keyStore: FakeApiKeyStore = FakeApiKeyStore(),
        usageStore: FakeApiUsageStore = FakeApiUsageStore()
    ) = SettingsViewModel(manager, keyStore, usageStore)

    @Test
    fun `toggling a followed category adds it then removes it`() = runTest {
        val manager = FakeSettingsManager()
        val vm = viewModel(manager)
        backgroundScope.launch { vm.settings.collect {} }

        vm.onEvent(SettingsEvent.ToggleFollowedCategory("business"))
        assertThat(manager.state.value.followedCategories).contains("business")

        vm.onEvent(SettingsEvent.ToggleFollowedCategory("business"))
        assertThat(manager.state.value.followedCategories).doesNotContain("business")
    }

    @Test
    fun `setting theme mode persists it`() = runTest {
        val manager = FakeSettingsManager()
        viewModel(manager).onEvent(SettingsEvent.SetThemeMode(ThemeMode.DARK))
        assertThat(manager.state.value.themeMode).isEqualTo(ThemeMode.DARK)
    }

    @Test
    fun `saving an api key exposes the source as configured`() = runTest {
        val keyStore = FakeApiKeyStore()
        val vm = viewModel(keyStore = keyStore)

        vm.onEvent(SettingsEvent.SetApiKey("gnews", "secret"))

        assertThat(keyStore.getKey("gnews")).isEqualTo("secret")
        // Await the derived StateFlow's emission rather than racing its WhileSubscribed start.
        assertThat(vm.configuredSourceIds.first { it.isNotEmpty() }).contains("gnews")
    }

    @Test
    fun `clearing the active source key falls back to another configured source`() = runTest {
        val manager = FakeSettingsManager()
        manager.setDataSource("gnews")
        val keyStore = FakeApiKeyStore(mapOf("gnews" to "k1", "newsdata" to "k2"))
        val vm = viewModel(manager = manager, keyStore = keyStore)

        vm.onEvent(SettingsEvent.ClearApiKey("gnews"))

        assertThat(keyStore.getKey("gnews")).isEmpty()
        assertThat(manager.state.value.dataSourceId).isEqualTo("newsdata")
    }

    @Test
    fun `clearing the only configured source falls back to the default`() = runTest {
        val manager = FakeSettingsManager()
        manager.setDataSource("gnews")
        val keyStore = FakeApiKeyStore(mapOf("gnews" to "k1"))
        val vm = viewModel(manager = manager, keyStore = keyStore)

        vm.onEvent(SettingsEvent.ClearApiKey("gnews"))

        assertThat(manager.state.value.dataSourceId).isEqualTo("newsapi")
    }
}
