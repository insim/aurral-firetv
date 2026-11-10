package org.aurral.firetv.ui.common

import org.junit.Assert.assertEquals
import org.junit.Test

class FormattingTest {

    @Test
    fun `formats seconds as minutes and seconds`() {
        assertEquals("1:05", formatDuration(65))
        assertEquals("0:07", formatDuration(7))
        assertEquals("10:00", formatDuration(600))
    }

    @Test
    fun `formats unknown durations as a placeholder`() {
        assertEquals("--:--", formatDuration(0))
        assertEquals("--:--", formatDuration(-5))
        assertEquals("--:--", formatDurationMs(0))
    }

    @Test
    fun `formats milliseconds`() {
        assertEquals("1:05", formatDurationMs(65_000))
        assertEquals("3:20", formatDurationMs(200_000))
    }
}
