package com.skystone1000.briefly.data.debug

import android.content.Context
import com.skystone1000.briefly.data.remote.sourceIdForHost
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Request
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton

/** Query-param names that hold credentials — never used in the mock folder name. */
private val KEY_PARAMS = setOf("apikey", "api_key", "api-key", "token", "access_key", "key")

/** Characters allowed in a mock path segment; everything else becomes '_'. */
private val UNSAFE = Regex("[^A-Za-z0-9=&._-]")

/**
 * Builds the relative mock path for a call: `sourceId/endpoint/param-slug`, e.g.
 * `newsapi/top-headlines/category=business`. Pure + testable. Credential params are excluded so
 * the folder name is stable and safe; remaining params are sorted so order doesn't matter.
 */
internal fun mockKey(
    sourceId: String,
    pathSegments: List<String>,
    queryParams: List<Pair<String, String>>,
): String {
    val endpoint = pathSegments.lastOrNull { it.isNotBlank() } ?: "root"
    val slug = queryParams
        .filter { it.first.lowercase() !in KEY_PARAMS }
        .sortedBy { it.first }
        .joinToString("&") { "${it.first}=${it.second}" }
        .ifBlank { "default" }
    return "${sanitize(sourceId)}/${sanitize(endpoint)}/${sanitize(slug)}".take(MAX_KEY_LEN)
}

private fun sanitize(s: String): String = UNSAFE.replace(s, "_")

private const val MAX_KEY_LEN = 180

/**
 * Captures and replays provider responses for debug offline mode.
 *
 * Capture dir: `getExternalFilesDir(null)/mock/<key>/NNN.json` (adb-pullable). [next] rotates
 * round-robin through captures for a key so each refresh yields a different saved response, and
 * falls back to bundled `assets/mock/<key>` when the device has no capture.
 */
@Singleton
class MockStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val rootDir: File get() = File(context.getExternalFilesDir(null), "mock")
    private val rotation = ConcurrentHashMap<String, AtomicInteger>()

    /** Relative mock key for a request, or null for non-provider hosts (e.g. image CDNs). */
    fun keyOf(request: Request): String? {
        val sourceId = sourceIdForHost(request.url.host) ?: return null
        val params = request.url.queryParameterNames.flatMap { name ->
            request.url.queryParameterValues(name).map { name to (it ?: "") }
        }
        return mockKey(sourceId, request.url.pathSegments, params)
    }

    /** Writes [json] as the next numbered capture for [key]. */
    fun save(key: String, json: String) {
        val dir = File(rootDir, key).apply { mkdirs() }
        val next = (dir.listFiles { f -> f.name.endsWith(".json") }?.size ?: 0) + 1
        File(dir, "%03d.json".format(next)).writeText(json)
    }

    /**
     * Next replay body for [key]: rotates through device captures, else bundled assets, else null.
     */
    fun next(key: String): String? {
        deviceFiles(key).takeIf { it.isNotEmpty() }?.let { files ->
            val i = indexFor(key, files.size)
            return files[i].readText()
        }
        assetFiles(key).takeIf { it.isNotEmpty() }?.let { names ->
            val i = indexFor(key, names.size)
            return runCatching {
                context.assets.open("mock/$key/${names[i]}").bufferedReader().use { it.readText() }
            }.getOrNull()
        }
        return null
    }

    /** Deletes all captured mocks from the device dir. */
    fun clearAll() {
        rootDir.deleteRecursively()
        rotation.clear()
    }

    private fun deviceFiles(key: String): List<File> =
        File(rootDir, key).listFiles { f -> f.name.endsWith(".json") }?.sortedBy { it.name } ?: emptyList()

    private fun assetFiles(key: String): List<String> =
        runCatching { context.assets.list("mock/$key")?.filter { it.endsWith(".json") }?.sorted() }
            .getOrNull() ?: emptyList()

    private fun indexFor(key: String, size: Int): Int {
        val counter = rotation.getOrPut(key) { AtomicInteger(0) }
        return (counter.getAndIncrement() % size + size) % size
    }
}
