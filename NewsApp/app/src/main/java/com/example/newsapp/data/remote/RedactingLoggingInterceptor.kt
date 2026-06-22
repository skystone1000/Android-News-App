package com.example.newsapp.data.remote

import android.util.Log
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Minimal request logger that **redacts API keys**. Provider keys travel as query parameters, and
 * OkHttp 4.x's `HttpLoggingInterceptor` cannot redact those — it would print them to Logcat. This
 * logs `method redacted-url -> code (ms)` at debug only, with sensitive query values masked.
 */
class RedactingLoggingInterceptor(private val enabled: Boolean) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!enabled) return chain.proceed(request)

        val start = System.nanoTime()
        val response = chain.proceed(request)
        val millis = (System.nanoTime() - start) / NANOS_PER_MILLI
        Log.d(TAG, "${request.method} ${redact(request.url)} -> ${response.code} (${millis}ms)")
        return response
    }

    private fun redact(url: HttpUrl): String {
        val builder = url.newBuilder()
        url.queryParameterNames.forEach { name ->
            if (name.lowercase() in SENSITIVE) builder.setQueryParameter(name, "***")
        }
        return builder.build().toString()
    }

    private companion object {
        const val TAG = "NewsHttp"
        const val NANOS_PER_MILLI = 1_000_000
        val SENSITIVE = setOf("apikey", "token", "access_key", "x-api-key")
    }
}
