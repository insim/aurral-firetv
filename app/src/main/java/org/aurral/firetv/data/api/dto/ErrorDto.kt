package org.aurral.firetv.data.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorDto(
    val code: Int = 0,
    val message: String? = null,
)
