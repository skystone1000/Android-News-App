package com.example.newsapp.domain.usecases.app_entry

import com.example.newsapp.domain.manager.LocalUserManager
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

/** In-memory [LocalUserManager] for fast, Android-free unit tests. */
private class FakeLocalUserManager : LocalUserManager {
    private val state = MutableStateFlow(false)
    override suspend fun saveAppEntry() { state.value = true }
    override fun readAppEntry(): Flow<Boolean> = state
}

class AppEntryUseCasesTest {

    @Test
    fun `app entry defaults to false`() = runTest {
        val readAppEntry = ReadAppEntry(FakeLocalUserManager())
        assertThat(readAppEntry().first()).isFalse()
    }

    @Test
    fun `saving app entry flips the flag to true`() = runTest {
        val manager = FakeLocalUserManager()
        val readAppEntry = ReadAppEntry(manager)
        val saveAppEntry = SaveAppEntry(manager)

        saveAppEntry()

        assertThat(readAppEntry().first()).isTrue()
    }
}
