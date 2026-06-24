package com.example.newsapp.data.remote

import com.example.newsapp.BuildConfig
import com.example.newsapp.data.debug.DebugConfigHolder
import com.example.newsapp.data.debug.MockStore
import com.example.newsapp.data.debug.RequestEntry
import com.example.newsapp.data.debug.RequestKind
import com.example.newsapp.data.debug.RequestLog
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val EMPTY_JSON = "{}"
private val KEY_PARAM_NAMES = setOf("apikey", "api_key", "api-key", "token", "access_key", "key")

/**
 * Debug-only OkHttp interceptor (first in the chain) implementing the mock tooling:
 * latency injection, forced errors, offline replay, and response capture. No-op in release and
 * for non-provider hosts (e.g. image CDNs). Placed before `UsageInterceptor` so replayed/offline
 * calls don't consume the free-tier quota.
 */
@Singleton
class MockInterceptor @Inject constructor(
    private val configHolder: DebugConfigHolder,
    private val mockStore: MockStore,
    private val requestLog: RequestLog,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val key = mockStore.keyOf(request)
        if (!BuildConfig.DEBUG || key == null) return chain.proceed(request)

        val cfg = configHolder.current
        if (cfg.latencyMs > 0) runCatching { Thread.sleep(cfg.latencyMs) }

        if (cfg.forceError) {
            log(request, status = null, durationMs = cfg.latencyMs, kind = RequestKind.ERROR)
            throw IOException("Forced debug error")
        }

        if (cfg.offlineMode) {
            val json = mockStore.next(key) ?: EMPTY_JSON
            log(request, status = 200, durationMs = cfg.latencyMs, kind = RequestKind.REPLAYED)
            return jsonResponse(request, json)
        }

        val start = System.currentTimeMillis()
        val response = chain.proceed(request)
        val duration = System.currentTimeMillis() - start

        if (cfg.saveResponses && response.isSuccessful) {
            val json = response.peekBody(Long.MAX_VALUE).string()
            runCatching { mockStore.save(key, json) }
            log(request, response.code, duration, RequestKind.SAVED)
        } else {
            log(request, response.code, duration, RequestKind.LIVE)
        }
        return response
    }

    private fun jsonResponse(request: Request, json: String): Response = Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_1_1)
        .code(200)
        .message("OK (mock)")
        .body(json.toResponseBody("application/json".toMediaType()))
        .build()

    private fun log(request: Request, status: Int?, durationMs: Long, kind: RequestKind) {
        requestLog.record(
            RequestEntry(
                timeMs = System.currentTimeMillis(),
                method = request.method,
                url = redact(request),
                status = status,
                durationMs = durationMs,
                kind = kind,
            )
        )
    }

    /** Masks credential query params so keys never appear in the inspector. */
    private fun redact(request: Request): String {
        var url = request.url.newBuilder()
        request.url.queryParameterNames.forEach { name ->
            if (name.lowercase() in KEY_PARAM_NAMES) {
                url = url.setQueryParameter(name, "***")
            }
        }
        return url.build().toString()
    }
}
