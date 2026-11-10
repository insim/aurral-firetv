package org.aurral.firetv.data.repository

import org.aurral.firetv.data.api.SubsonicApi
import org.aurral.firetv.data.api.orThrow
import org.aurral.firetv.data.mapper.toLyrics
import org.aurral.firetv.data.model.LyricLine
import org.aurral.firetv.data.model.Lyrics
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Lyrics, preferring the OpenSubsonic `songLyrics` extension (which can include
 * timings) and falling back to the classic artist/title lookup.
 */
@Singleton
class LyricsRepository @Inject constructor(
    private val api: SubsonicApi,
) {

    suspend fun lyrics(songId: String): Lyrics {
        val structured = runCatching {
            api.getLyricsBySongId(songId).orThrow().lyricsList?.toLyrics()
        }.getOrNull()
        if (structured != null && !structured.isEmpty) return structured

        val song = runCatching { api.getSong(songId).orThrow().song }.getOrNull() ?: return Lyrics.EMPTY
        val plain = runCatching {
            api.getLyrics(
                artist = song.artist.orEmpty(),
                title = song.title.orEmpty(),
            ).orThrow().lyrics?.value
        }.getOrNull()

        return if (plain.isNullOrBlank()) {
            Lyrics.EMPTY
        } else {
            Lyrics(
                synced = false,
                lines = plain.lines().map { LyricLine(startMs = null, text = it) },
            )
        }
    }
}
