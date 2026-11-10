package org.aurral.firetv.data.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Root JSON object: every Subsonic response is wrapped in `subsonic-response`. */
@Serializable
data class SubsonicEnvelope(
    @SerialName("subsonic-response") val response: SubsonicResponseDto,
)
