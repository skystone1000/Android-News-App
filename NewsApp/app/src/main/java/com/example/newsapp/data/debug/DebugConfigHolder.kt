package com.example.newsapp.data.debug

import com.example.newsapp.domain.debug.DebugConfig
import com.example.newsapp.domain.debug.DebugSettingsStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Keeps the latest [DebugConfig] in an [AtomicReference] so the OkHttp `MockInterceptor` can read
 * it synchronously on the network thread (DataStore is suspending and must not be read there).
 */
@Singleton
class DebugConfigHolder @Inject constructor(
    store: DebugSettingsStore,
) {
    private val ref = AtomicReference(DebugConfig())

    init {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        store.config().onEach { ref.set(it) }.launchIn(scope)
    }

    val current: DebugConfig get() = ref.get()
}
