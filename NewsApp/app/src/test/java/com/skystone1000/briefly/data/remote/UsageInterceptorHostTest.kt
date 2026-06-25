package com.skystone1000.briefly.data.remote

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class UsageInterceptorHostTest {

    @Test
    fun `known provider hosts map to their source id`() {
        assertThat(sourceIdForHost("newsapi.org")).isEqualTo("newsapi")
        assertThat(sourceIdForHost("newsdata.io")).isEqualTo("newsdata")
        assertThat(sourceIdForHost("gnews.io")).isEqualTo("gnews")
        assertThat(sourceIdForHost("api.currentsapi.services")).isEqualTo("currents")
        assertThat(sourceIdForHost("api.mediastack.com")).isEqualTo("mediastack")
    }

    @Test
    fun `unknown hosts map to null`() {
        assertThat(sourceIdForHost("images.example.com")).isNull()
    }
}
