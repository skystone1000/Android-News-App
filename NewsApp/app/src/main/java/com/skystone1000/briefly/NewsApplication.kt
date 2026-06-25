package com.skystone1000.briefly

import android.app.Application
import android.os.StrictMode
import com.skystone1000.briefly.data.notifications.NewsNotifier
import com.skystone1000.briefly.data.remote.source.NewsSourceProvider
import com.skystone1000.briefly.domain.manager.SettingsManager
import com.skystone1000.briefly.domain.security.ApiKeyStore
import com.skystone1000.briefly.work.DigestScheduler
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Application entry point. `@HiltAndroidApp` triggers Hilt code generation and creates the
 * application-level dependency container. Also keeps the active news source in sync with
 * the user's chosen data source.
 */
@HiltAndroidApp
class NewsApplication : Application() {

    @Inject
    lateinit var settingsManager: SettingsManager

    @Inject
    lateinit var newsSourceProvider: NewsSourceProvider

    @Inject
    lateinit var apiKeyStore: ApiKeyStore

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) enableStrictMode()
        settingsManager.settings()
            .onEach { newsSourceProvider.activeSourceId = it.dataSourceId }
            .launchIn(appScope)

        NewsNotifier.ensureChannel(this)
        DigestScheduler.schedule(this)
        seedDevKeysFromBuildConfig()
    }

    /**
     * Dev convenience: if a provider has no user-entered key yet but one is present in
     * `local.properties` (via `BuildConfig`), seed it so the existing dev flow keeps working.
     * Production builds ship with blank BuildConfig keys, so this is a no-op there.
     */
    private fun seedDevKeysFromBuildConfig() {
        appScope.launch {
            val configured = apiKeyStore.keys().first().keys
            if ("newsapi" !in configured && BuildConfig.NEWS_API_KEY.isNotBlank()) {
                apiKeyStore.setKey("newsapi", BuildConfig.NEWS_API_KEY)
            }
            if ("gnews" !in configured && BuildConfig.GNEWS_API_KEY.isNotBlank()) {
                apiKeyStore.setKey("gnews", BuildConfig.GNEWS_API_KEY)
            }
        }
    }

    /** Surfaces accidental disk/network on the main thread and common leaks during development. */
    private fun enableStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .build()
        )
    }
}
