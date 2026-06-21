package com.example.newsapp.data.remote.source

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Resolves the currently-active [NewsSource]. Sources are supplied by Hilt as a map keyed
 * by [NewsSource.id]. Phase 4 wires [activeSourceId] to a user preference; for now it
 * falls back to [DEFAULT_SOURCE_ID].
 */
@Singleton
class NewsSourceProvider @Inject constructor(
    private val sources: Map<String, @JvmSuppressWildcards NewsSource>
) {

    @Volatile
    var activeSourceId: String = DEFAULT_SOURCE_ID

    fun availableSourceIds(): Set<String> = sources.keys

    fun activeSource(): NewsSource =
        sources[activeSourceId]
            ?: sources[DEFAULT_SOURCE_ID]
            ?: sources.values.firstOrNull()
            ?: error("No NewsSource implementations are registered")

    companion object {
        const val DEFAULT_SOURCE_ID = NewsApiSource.ID
    }
}
