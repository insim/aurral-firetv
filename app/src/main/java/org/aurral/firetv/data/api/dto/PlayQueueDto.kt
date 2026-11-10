package org.aurral.firetv.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class PlayQueueDto(
    val entry: List<ChildDto> = emptyList(),
    val current: String? = null,
    val position: Long? = null,
    val username: String? = null,
    val changed: String? = null,
)
