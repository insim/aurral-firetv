package org.aurral.firetv.playback

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScrobbleRulesTest {

    @Test
    fun `does not submit before half of a long track`() {
        // 10 minute track, threshold is the 4 minute cap
        assertFalse(ScrobbleRules.shouldSubmit(positionMs = 200_000, durationMs = 600_000))
    }

    @Test
    fun `submits once past half of a long track`() {
        assertTrue(ScrobbleRules.shouldSubmit(positionMs = 300_000, durationMs = 600_000))
    }

    @Test
    fun `submits at the four minute cap for very long tracks`() {
        // 20 minute track: threshold is capped at 4 minutes
        assertFalse(ScrobbleRules.shouldSubmit(positionMs = 239_000, durationMs = 1_200_000))
        assertTrue(ScrobbleRules.shouldSubmit(positionMs = 240_000, durationMs = 1_200_000))
    }

    @Test
    fun `submits after half of a short track`() {
        assertFalse(ScrobbleRules.shouldSubmit(positionMs = 29_000, durationMs = 60_000))
        assertTrue(ScrobbleRules.shouldSubmit(positionMs = 30_000, durationMs = 60_000))
    }

    @Test
    fun `falls back to the four minute cap when the duration is unknown`() {
        assertFalse(ScrobbleRules.shouldSubmit(positionMs = 60_000, durationMs = 0))
        assertTrue(ScrobbleRules.shouldSubmit(positionMs = 240_000, durationMs = 0))
    }
}
