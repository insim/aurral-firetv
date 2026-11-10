package org.aurral.firetv.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class AlbumListDto(
    val album: List<AlbumDto> = emptyList(),
)
