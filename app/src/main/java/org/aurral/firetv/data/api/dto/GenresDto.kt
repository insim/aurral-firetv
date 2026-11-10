package org.aurral.firetv.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class GenresDto(
    val genre: List<GenreDto> = emptyList(),
)
