package com.example.newsapp.domain.usecases.app_entry

/**
 * Groups the app-entry use cases so they can be injected as a single dependency.
 */
data class AppEntryUseCases(
    val readAppEntry: ReadAppEntry,
    val saveAppEntry: SaveAppEntry
)
