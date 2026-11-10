package org.aurral.firetv.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class PlaylistsDto(
    val playlist: List<PlaylistDto> = emptyList(),
)
