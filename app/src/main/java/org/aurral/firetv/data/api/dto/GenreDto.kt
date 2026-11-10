package org.aurral.firetv.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class GenreDto(
    val value: String = "",
    val songCount: Int? = null,
    val albumCount: Int? = null,
)
