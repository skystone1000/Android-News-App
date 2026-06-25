package com.skystone1000.briefly.data.remote.source

/**
 * Cursor helpers for providers that page by an integer page number. The [NewsSource] cursor is a
 * string, so a numeric page is encoded as its decimal text ("1", "2", …).
 */

/** Decodes a numeric page cursor; a null/garbage cursor starts at page 1. */
internal fun pageOf(cursor: String?): Int = cursor?.toIntOrNull() ?: 1

/** Next page cursor, or null when the last load returned nothing (end of results). */
internal fun nextPageCursor(page: Int, loaded: List<*>): String? =
    if (loaded.isEmpty()) null else (page + 1).toString()
