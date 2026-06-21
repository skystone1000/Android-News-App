package com.example.newsapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point. `@HiltAndroidApp` triggers Hilt code generation and
 * creates the application-level dependency container.
 */
@HiltAndroidApp
class NewsApplication : Application()
