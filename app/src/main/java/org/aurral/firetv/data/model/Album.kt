package org.aurral.firetv.data.model

data class Album(
    val id: String,
    val name: String,
    val artist: String,
    val artistId: String?,
    val coverArtId: String?,
    val songCount: Int,
    val durationSeconds: Int,
    val year: Int?,
    val genre: String?,
    val starred: Boolean,
    val userRating: Int?,
    val songs: List<Song> = emptyList(),
) {
    val hasCoverArt: Boolean get() = !coverArtId.isNullOrBlank()
}
