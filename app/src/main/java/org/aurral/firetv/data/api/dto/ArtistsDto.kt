package org.aurral.firetv.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ArtistsDto(
    val ignoredArticles: String? = null,
    val index: List<IndexDto> = emptyList(),
)
