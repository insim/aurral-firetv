package org.aurral.firetv.data.mapper

import org.aurral.firetv.data.api.dto.PlayQueueDto
import org.aurral.firetv.data.model.PlayQueue

fun PlayQueueDto.toPlayQueue(): PlayQueue = PlayQueue(
    songs = entry.toSongs(),
    currentSongId = current,
    positionMs = position ?: 0L,
)
