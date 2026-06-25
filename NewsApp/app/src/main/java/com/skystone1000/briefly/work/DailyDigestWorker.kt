package com.skystone1000.briefly.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.skystone1000.briefly.data.notifications.NewsNotifier
import com.skystone1000.briefly.data.remote.source.NewsSourceProvider
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

/**
 * Periodic job that fetches a few top headlines from the active source and posts a digest
 * notification. Dependencies are pulled from Hilt via an [EntryPoint] (workers aren't
 * constructed by Hilt here), keeping the WorkManager wiring minimal.
 */
class DailyDigestWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface DigestEntryPoint {
        fun newsSourceProvider(): NewsSourceProvider
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    override suspend fun doWork(): Result {
        return try {
            val provider = EntryPointAccessors
                .fromApplication(applicationContext, DigestEntryPoint::class.java)
                .newsSourceProvider()

            val headlines = provider.activeSource()
                .getNews(category = null, cursor = null, pageSize = 5)
                .articles
            if (headlines.isEmpty()) return Result.success()

            val top = headlines.first().title
            val more = headlines.size - 1
            val body = if (more > 0) "$top  (+$more more headlines)" else top
            NewsNotifier.showDigest(applicationContext, title = "Today's headlines", body = body)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "daily_digest"
    }
}
