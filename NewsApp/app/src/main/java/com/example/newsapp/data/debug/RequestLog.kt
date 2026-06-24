package com.example.newsapp.data.debug

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/** How a request was served (for the debug request inspector). */
enum class RequestKind { LIVE, SAVED, REPLAYED, ERROR }

data class RequestEntry(
    val timeMs: Long,
    val method: String,
    val url: String,
    val status: Int?,
    val durationMs: Long,
    val kind: RequestKind,
)

/** In-memory ring buffer of recent provider requests, surfaced in the debug request inspector. */
@Singleton
class RequestLog @Inject constructor() {

    private val _entries = MutableStateFlow<List<RequestEntry>>(emptyList())
    val entries: StateFlow<List<RequestEntry>> = _entries

    fun record(entry: RequestEntry) {
        _entries.update { (listOf(entry) + it).take(CAPACITY) }
    }

    fun clear() {
        _entries.value = emptyList()
    }

    private companion object {
        const val CAPACITY = 100
    }
}
