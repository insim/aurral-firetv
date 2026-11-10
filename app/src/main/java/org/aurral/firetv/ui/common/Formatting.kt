package org.aurral.firetv.ui.common

/** `m:ss` from seconds. */
fun formatDuration(totalSeconds: Int): String {
    if (totalSeconds <= 0) return "--:--"
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

/** `m:ss` from milliseconds, tolerating unknown (non-positive) durations. */
fun formatDurationMs(millis: Long): String =
    if (millis <= 0L) "--:--" else formatDuration((millis / 1000L).toInt())
