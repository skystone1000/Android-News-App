package com.skystone1000.briefly.data.remote.source

/**
 * Thrown by a [NewsSource] when the user has not configured an API key for [sourceId].
 * Surfaced through the paging error state so the UI can prompt the user to add a key in Settings.
 */
class MissingApiKeyException(val sourceId: String) :
    Exception("No API key configured for source '$sourceId'")
