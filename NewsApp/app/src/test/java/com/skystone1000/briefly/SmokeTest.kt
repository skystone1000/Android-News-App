package com.skystone1000.briefly

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Phase 0 smoke test — proves the unit-test harness (JUnit + Truth) is wired and
 * runs in CI. Replaced/augmented by real tests in later phases.
 */
class SmokeTest {

    @Test
    fun `test harness executes`() {
        assertThat(1 + 1).isEqualTo(2)
    }
}
