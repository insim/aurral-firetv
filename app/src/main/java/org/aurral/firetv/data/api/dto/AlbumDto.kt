package org.aurral.firetv.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class AlbumDto(
    val id: String,
    val name: String? = null,
    val title: String? = null,
    val artist: String? = null,
    val artistId: String? = null,
    val coverArt: String? = null,
    val songCount: Int? = null,
    val duration: Int? = null,
    val playCount: Int? = null,
    val created: String? = null,
    val starred: String? = null,
    val year: Int? = null,
    val genre: String? = null,
    val userRating: Int? = null,
    val song: List<ChildDto> = emptyList(),
)
