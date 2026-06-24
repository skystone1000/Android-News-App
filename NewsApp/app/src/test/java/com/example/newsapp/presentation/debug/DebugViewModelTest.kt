package com.example.newsapp.presentation.debug

import com.example.newsapp.data.debug.DebugActions
import com.example.newsapp.util.FakeDebugSettingsStore
import com.example.newsapp.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DebugViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val actions = mockk<DebugActions>(relaxed = true)

    private fun viewModel(store: FakeDebugSettingsStore) = DebugViewModel(store, actions)

    @Test
    fun `toggles persist to the store`() = runTest {
        val store = FakeDebugSettingsStore()
        val vm = viewModel(store)

        vm.setSaveResponses(true)
        vm.setOfflineMode(true)
        vm.setForceError(true)
        advanceUntilIdle()

        val cfg = store.config().first()
        assertThat(cfg.saveResponses).isTrue()
        assertThat(cfg.offlineMode).isTrue()
        assertThat(cfg.forceError).isTrue()
    }

    @Test
    fun `cycleLatency advances through presets and wraps`() = runTest {
        val store = FakeDebugSettingsStore()
        val vm = viewModel(store)
        backgroundScope.launch { vm.config.collect {} }
        advanceUntilIdle()

        val seen = mutableListOf<Long>()
        repeat(LATENCY_PRESETS.size) {
            vm.cycleLatency()
            advanceUntilIdle()
            seen += store.config().first().latencyMs
        }
        // From 0 -> 500 -> 1500 -> 3000 -> back to 0.
        assertThat(seen).containsExactly(500L, 1500L, 3000L, 0L).inOrder()
    }

    @Test
    fun `clearMocks runs the action and sets a side effect`() = runTest {
        val vm = viewModel(FakeDebugSettingsStore())
        vm.clearMocks()
        advanceUntilIdle()

        coVerify { actions.clearMocks() }
        assertThat(vm.sideEffect).isEqualTo("Captured mocks cleared")
    }

    @Test
    fun `resetAppState runs the action and sets a side effect`() = runTest {
        val vm = viewModel(FakeDebugSettingsStore())
        vm.resetAppState()
        advanceUntilIdle()

        coVerify { actions.resetAppState() }
        assertThat(vm.sideEffect).isEqualTo("App state reset")
    }
}
