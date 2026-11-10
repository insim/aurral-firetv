package org.aurral.firetv.data.model

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: String?,
    val artistId: String?,
    val coverArtId: String?,
    val durationSeconds: Int,
    val trackNumber: Int?,
    val discNumber: Int?,
    val year: Int?,
    val genre: String?,
    val starred: Boolean,
    val userRating: Int?,
    val suffix: String?,
    val bitRate: Int?,
) {
    val hasCoverArt: Boolean get() = !coverArtId.isNullOrBlank()
}
