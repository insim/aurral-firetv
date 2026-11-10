package org.aurral.firetv.data.api

import org.aurral.firetv.data.api.dto.SubsonicEnvelope
import org.aurral.firetv.data.api.dto.SubsonicResponseDto

/** Unwraps an envelope, throwing [SubsonicException] when the server reported failure. */
fun SubsonicEnvelope.orThrow(): SubsonicResponseDto = response.orThrow()

/** Throws [SubsonicException] when the server answered with `status="failed"`. */
fun SubsonicResponseDto.orThrow(): SubsonicResponseDto {
    if (status != "ok") {
        throw SubsonicException(
            code = error?.code ?: 0,
            serverMessage = error?.message,
        )
    }
    return this
}
