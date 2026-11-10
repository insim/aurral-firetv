package org.aurral.firetv.data.api.dto

import kotlinx.serialization.Serializable

/** One letter group in a [ArtistsDto] result. */
@Serializable
data class IndexDto(
    val name: String = "#",
    val artist: List<ArtistDto> = emptyList(),
)
