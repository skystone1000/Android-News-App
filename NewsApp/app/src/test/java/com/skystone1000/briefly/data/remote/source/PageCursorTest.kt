package com.skystone1000.briefly.data.remote.source

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PageCursorTest {

    @Test
    fun `null or garbage cursor starts at page 1`() {
        assertThat(pageOf(null)).isEqualTo(1)
        assertThat(pageOf("")).isEqualTo(1)
        assertThat(pageOf("abc")).isEqualTo(1)
    }

    @Test
    fun `numeric cursor decodes to its page`() {
        assertThat(pageOf("3")).isEqualTo(3)
    }

    @Test
    fun `next cursor advances while results exist and ends on empty`() {
        assertThat(nextPageCursor(1, listOf("a"))).isEqualTo("2")
        assertThat(nextPageCursor(5, listOf("a", "b"))).isEqualTo("6")
        assertThat(nextPageCursor(1, emptyList<String>())).isNull()
    }
}
