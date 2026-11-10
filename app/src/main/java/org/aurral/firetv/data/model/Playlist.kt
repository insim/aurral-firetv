package org.aurral.firetv.data.model

/**
 * A Subsonic playlist. Aurral exposes both static playlists and generated
 * "flows" through the same endpoints; flows reject edits with error 50.
 */
data class Playlist(
    val id: String,
    val name: String,
    val owner: String?,
    val comment: String?,
    val songCount: Int,
    val durationSeconds: Int,
    val coverArtId: String?,
    val songs: List<Song> = emptyList(),
) {
    val hasCoverArt: Boolean get() = !coverArtId.isNullOrBlank()
}
