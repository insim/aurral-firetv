package org.aurral.firetv.data.repository

import org.aurral.firetv.data.api.SubsonicApi
import org.aurral.firetv.data.api.orThrow
import org.aurral.firetv.data.mapper.toLyrics
import org.aurral.firetv.data.model.Lyrics
import javax.inject.Inject
import javax.inject.Singleton

/** Lyrics via the OpenSubsonic `songLyrics` extension (timed when available). */
@Singleton
class LyricsRepository @Inject constructor(
    private val api: SubsonicApi,
) {

    suspend fun lyrics(songId: String): Lyrics =
        api.getLyricsBySongId(songId).orThrow().lyricsList?.toLyrics() ?: Lyrics.EMPTY
}
