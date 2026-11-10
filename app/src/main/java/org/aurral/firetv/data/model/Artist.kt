package org.aurral.firetv.data.model

data class Artist(
    val id: String,
    val name: String,
    val albumCount: Int,
    val coverArtId: String?,
    val starred: Boolean,
    val albums: List<Album> = emptyList(),
) {
    val hasCoverArt: Boolean get() = !coverArtId.isNullOrBlank()
}
