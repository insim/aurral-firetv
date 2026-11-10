package org.aurral.firetv.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ArtistInfo2Dto(
    val biography: String? = null,
    val musicBrainzId: String? = null,
    val lastFmUrl: String? = null,
    val smallImageUrl: String? = null,
    val mediumImageUrl: String? = null,
    val largeImageUrl: String? = null,
    val similarArtist: List<ArtistDto> = emptyList(),
)
