package org.aurral.firetv.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class LyricsListDto(
    val structuredLyrics: List<StructuredLyricsDto> = emptyList(),
)
