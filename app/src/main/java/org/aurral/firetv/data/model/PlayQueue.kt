package org.aurral.firetv.data.model

/** A queue saved on the server via `savePlayQueue`, used to resume playback. */
data class PlayQueue(
    val songs: List<Song>,
    val currentSongId: String?,
    val positionMs: Long,
) {
    val isEmpty: Boolean get() = songs.isEmpty()
}
