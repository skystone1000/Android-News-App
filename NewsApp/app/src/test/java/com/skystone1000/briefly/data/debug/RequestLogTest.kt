package com.skystone1000.briefly.data.debug

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RequestLogTest {

    private fun entry(id: Int) = RequestEntry(
        timeMs = id.toLong(),
        method = "GET",
        url = "https://example.com/$id",
        status = 200,
        durationMs = 1,
        kind = RequestKind.LIVE,
    )

    @Test
    fun `record prepends newest first`() {
        val log = RequestLog()
        log.record(entry(1))
        log.record(entry(2))
        assertThat(log.entries.value.map { it.timeMs }).containsExactly(2L, 1L).inOrder()
    }

    @Test
    fun `buffer is capped at 100 entries`() {
        val log = RequestLog()
        repeat(105) { log.record(entry(it)) }
        assertThat(log.entries.value).hasSize(100)
        // Newest (104) is first; oldest retained is 5.
        assertThat(log.entries.value.first().timeMs).isEqualTo(104L)
        assertThat(log.entries.value.last().timeMs).isEqualTo(5L)
    }

    @Test
    fun `clear empties the buffer`() {
        val log = RequestLog()
        log.record(entry(1))
        log.clear()
        assertThat(log.entries.value).isEmpty()
    }
}
