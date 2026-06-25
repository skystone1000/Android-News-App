package com.skystone1000.briefly.domain.usage

import com.skystone1000.briefly.domain.model.QuotaPeriod
import java.util.Calendar

/**
 * Computes quota window boundaries in the device's local time. A DAILY window starts at local
 * midnight; a MONTHLY window starts on the 1st of the month. Pure (no Android deps) and testable.
 */
object QuotaWindow {

    /** Start-of-window timestamp containing [now]. */
    fun windowStart(period: QuotaPeriod, now: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = now
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (period == QuotaPeriod.MONTHLY) set(Calendar.DAY_OF_MONTH, 1)
        }
        return cal.timeInMillis
    }

    /** Start of the next window after the one containing [now]. */
    fun resetAt(period: QuotaPeriod, now: Long): Long {
        val cal = Calendar.getInstance().apply { timeInMillis = windowStart(period, now) }
        when (period) {
            QuotaPeriod.DAILY -> cal.add(Calendar.DAY_OF_MONTH, 1)
            QuotaPeriod.MONTHLY -> cal.add(Calendar.MONTH, 1)
        }
        return cal.timeInMillis
    }
}
