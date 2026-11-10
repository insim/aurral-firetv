package org.aurral.firetv.data.model

/** Artist biography, when the server can provide one. */
data class ArtistBio(
    val biography: String,
    val imageUrl: String?,
)
