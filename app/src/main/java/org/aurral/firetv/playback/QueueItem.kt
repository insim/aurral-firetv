package org.aurral.firetv.playback

/** One entry of the playback queue, derived from a Media3 [androidx.media3.common.MediaItem]. */
data class QueueItem(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val coverArtId: String?,
    val durationMs: Long,
)
