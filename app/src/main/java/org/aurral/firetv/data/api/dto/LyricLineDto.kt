package org.aurral.firetv.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class LyricLineDto(
    val start: Long? = null,
    val value: String = "",
)
