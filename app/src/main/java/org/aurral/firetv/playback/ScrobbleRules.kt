package org.aurral.firetv.playback

/** Subsonic/GNU-style scrobble rule: report after half the track or four minutes. */
object ScrobbleRules {

    const val MAX_THRESHOLD_MS: Long = 4 * 60 * 1000L

    fun shouldSubmit(positionMs: Long, durationMs: Long): Boolean {
        val threshold = if (durationMs > 0L) minOf(durationMs / 2, MAX_THRESHOLD_MS) else MAX_THRESHOLD_MS
        return positionMs >= threshold
    }
}
