package org.aurral.firetv.data.mapper

import org.aurral.firetv.data.api.dto.LyricsListDto
import org.aurral.firetv.data.model.LyricLine
import org.aurral.firetv.data.model.Lyrics

/** Uses the first structured lyrics entry; Aurral returns at most one per track. */
fun LyricsListDto.toLyrics(): Lyrics {
    val lyrics = structuredLyrics.firstOrNull() ?: return Lyrics.EMPTY
    return Lyrics(
        synced = lyrics.synced,
        lines = lyrics.line.map { LyricLine(startMs = it.start, text = it.value) },
    )
}
