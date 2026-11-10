package org.aurral.firetv.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class StructuredLyricsDto(
    val displayArtist: String? = null,
    val displayTitle: String? = null,
    val lang: String? = null,
    val synced: Boolean = false,
    val line: List<LyricLineDto> = emptyList(),
)
