package org.aurral.firetv.data.api.dto

import kotlinx.serialization.Serializable

/** A plain list of songs, used by `getSongsByGenre` and `getTopSongs`. */
@Serializable
data class SongsDto(
    val song: List<ChildDto> = emptyList(),
)
