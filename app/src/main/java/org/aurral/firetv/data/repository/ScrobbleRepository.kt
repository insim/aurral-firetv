package org.aurral.firetv.data.repository

import org.aurral.firetv.data.api.SubsonicApi
import org.aurral.firetv.data.api.orThrow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Reports playback to the server.
 *
 * Aurral records the play in its own history and forwards it to the user's
 * connected scrobbling services (Last.fm, ListenBrainz, Koito), so the app never
 * contacts those services directly.
 */
@Singleton
class ScrobbleRepository @Inject constructor(
    private val api: SubsonicApi,
) {

    /** "Now playing" notification (no submission). */
    suspend fun nowPlaying(songId: String) {
        api.scrobble(id = songId, submission = false).orThrow()
    }

    /** A completed play. */
    suspend fun scrobble(songId: String, playedAtMs: Long) {
        api.scrobble(id = songId, time = playedAtMs, submission = true).orThrow()
    }
}
