package org.aurral.firetv.playback

/** Snapshot of the player exposed to the UI. */
data class PlaybackState(
    val isConnected: Boolean = false,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val isEnded: Boolean = false,
    val currentSongId: String? = null,
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val coverArtId: String? = null,
    val durationMs: Long = 0L,
    val positionMs: Long = 0L,
    val bufferedMs: Long = 0L,
    val shuffleEnabled: Boolean = false,
    val repeatMode: Int = 0,
    val queue: List<QueueItem> = emptyList(),
    val currentIndex: Int = -1,
) {
    val hasCurrent: Boolean get() = currentSongId != null
}
