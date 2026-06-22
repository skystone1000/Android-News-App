package com.example.newsapp.data.remote

import com.example.newsapp.domain.usage.ApiUsageStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response

/** Maps a request host to a provider id, or null for non-provider hosts (e.g. image CDNs). */
internal fun sourceIdForHost(host: String): String? = when {
    host.contains("newsapi.org") -> "newsapi"
    host.contains("newsdata.io") -> "newsdata"
    host.contains("gnews.io") -> "gnews"
    host.contains("currentsapi") -> "currents"
    host.contains("mediastack") -> "mediastack"
    else -> null
}

/**
 * Records one usage tick per provider request. Counting is fire-and-forget on [scope] so the
 * network thread isn't blocked by the DataStore write. If the response carries a recognized
 * `x-ratelimit-remaining` header, that reconciles the local estimate.
 */
class UsageInterceptor(
    private val usageStore: ApiUsageStore,
    private val scope: CoroutineScope
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val sourceId = sourceIdForHost(chain.request().url.host)
        if (sourceId != null) {
            val remaining = response.header("x-ratelimit-remaining")?.toIntOrNull()
            scope.launch {
                if (remaining != null) {
                    usageStore.recordRemaining(sourceId, remaining)
                } else {
                    usageStore.recordRequest(sourceId)
                }
            }
        }
        return response
    }
}
