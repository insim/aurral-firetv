package org.aurral.firetv.data.api.dto

import kotlinx.serialization.Serializable

/** Plain (unsynced) lyrics from the classic `getLyrics` endpoint. */
@Serializable
data class LyricsDto(
    val artist: String? = null,
    val title: String? = null,
    val value: String? = null,
)
