package org.aurral.firetv.data.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistDto(
    val id: String,
    val name: String? = null,
    val comment: String? = null,
    val owner: String? = null,
    @SerialName("public") val isPublic: Boolean? = null,
    val songCount: Int? = null,
    val duration: Int? = null,
    val coverArt: String? = null,
    val created: String? = null,
    val changed: String? = null,
    val entry: List<ChildDto> = emptyList(),
)
