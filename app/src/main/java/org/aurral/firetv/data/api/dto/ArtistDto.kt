package org.aurral.firetv.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ArtistDto(
    val id: String,
    val name: String? = null,
    val albumCount: Int? = null,
    val coverArt: String? = null,
    val starred: String? = null,
    val userRating: Int? = null,
    val album: List<AlbumDto> = emptyList(),
)
