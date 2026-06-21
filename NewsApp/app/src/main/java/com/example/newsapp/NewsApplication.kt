package com.example.newsapp

import android.app.Application
import com.example.newsapp.data.remote.source.NewsSourceProvider
import com.example.newsapp.domain.manager.SettingsManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        settingsManager.settings()
            .onEach { newsSourceProvider.activeSourceId = it.dataSourceId }
            .launchIn(appScope)
    }
}
