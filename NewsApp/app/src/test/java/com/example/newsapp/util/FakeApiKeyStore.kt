package com.example.newsapp.util

import com.example.newsapp.domain.security.ApiKeyStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** In-memory [ApiKeyStore] for tests (no encryption / Android dependencies). */
class FakeApiKeyStore(
    initial: Map<String, String> = emptyMap()
) : ApiKeyStore {

    private val store = HashMap(initial)
    private val flow = MutableStateFlow(configured())

    override fun keys(): Flow<Map<String, String>> = flow

    override suspend fun getKey(sourceId: String): String = store[sourceId].orEmpty()

    override suspend fun setKey(sourceId: String, key: String) {
        store[sourceId] = key.trim()
        flow.value = configured()
    }

    override suspend fun clearKey(sourceId: String) {
        store.remove(sourceId)
        flow.value = configured()
    }

    private fun configured(): Map<String, String> = store.filterValues { it.isNotBlank() }
}
