package com.example.newsapp.domain.usage

import com.example.newsapp.domain.model.QuotaPeriod
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.util.concurrent.TimeUnit

class QuotaWindowTest {

    private val now = 1_750_000_000_000L // a fixed instant in 2025

    @Test
    fun `daily window contains now and resets within a day`() {
        val start = QuotaWindow.windowStart(QuotaPeriod.DAILY, now)
        val reset = QuotaWindow.resetAt(QuotaPeriod.DAILY, now)

        assertThat(start).isAtMost(now)
        assertThat(reset).isGreaterThan(now)
        val span = reset - start
        // 23–25h tolerates DST transitions.
        assertThat(span).isAtLeast(TimeUnit.HOURS.toMillis(23))
        assertThat(span).isAtMost(TimeUnit.HOURS.toMillis(25))
    }

    @Test
    fun `monthly window spans at least 28 days`() {
        val start = QuotaWindow.windowStart(QuotaPeriod.MONTHLY, now)
        val reset = QuotaWindow.resetAt(QuotaPeriod.MONTHLY, now)

        assertThat(start).isAtMost(now)
        assertThat(reset).isGreaterThan(now)
        assertThat(reset - start).isAtLeast(TimeUnit.DAYS.toMillis(28))
    }
}
