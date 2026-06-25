package com.skystone1000.briefly.data.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.skystone1000.briefly.domain.model.SourceCatalog
import com.skystone1000.briefly.domain.security.ApiKeyStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * [ApiKeyStore] backed by [EncryptedSharedPreferences] — values are encrypted with an
 * Android Keystore-backed master key (AES-256-GCM), so keys never sit in cleartext on disk.
 * Reads are synchronous; [keys] is mirrored into a [MutableStateFlow] updated on every write.
 */
class EncryptedApiKeyStore(context: Context) : ApiKeyStore {

    private val prefs: SharedPreferences = run {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private val keysFlow = MutableStateFlow(loadConfigured())

    override fun keys(): Flow<Map<String, String>> = keysFlow.asStateFlow()

    override suspend fun getKey(sourceId: String): String =
        prefs.getString(sourceId, "").orEmpty()

    override suspend fun setKey(sourceId: String, key: String) {
        prefs.edit().putString(sourceId, key.trim()).apply()
        keysFlow.value = loadConfigured()
    }

    override suspend fun clearKey(sourceId: String) {
        prefs.edit().remove(sourceId).apply()
        keysFlow.value = loadConfigured()
    }

    private fun loadConfigured(): Map<String, String> =
        SourceCatalog.ALL_SOURCES
            .associate { it.id to prefs.getString(it.id, "").orEmpty() }
            .filterValues { it.isNotBlank() }

    private companion object {
        const val FILE_NAME = "api_keys"
    }
}
