package com.example.newsapp.domain.security

import kotlinx.coroutines.flow.Flow

/**
 * Stores per-provider API keys entered by the user, encrypted at rest. Keys are read at call time
 * by each `NewsSource` (they are not baked into the build). [keys] emits only providers that
 * currently have a non-blank key, so the UI can gate source selection on configured providers.
 */
interface ApiKeyStore {

    /** sourceId -> non-blank key, for every configured provider. */
    fun keys(): Flow<Map<String, String>>

    /** Current key for [sourceId], or empty string if none. */
    suspend fun getKey(sourceId: String): String

    suspend fun setKey(sourceId: String, key: String)

    suspend fun clearKey(sourceId: String)
}
