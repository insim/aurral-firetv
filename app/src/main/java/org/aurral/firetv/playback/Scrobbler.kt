package org.aurral.firetv.playback

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.aurral.firetv.data.repository.ScrobbleRepository
import org.aurral.firetv.di.qualifiers.ApplicationScope
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Turns player events into Subsonic `scrobble` calls.
 *
 * Aurral records the play in its own history and forwards it to the user's
 * connected scrobbling services, so the app only reports to the server.
 */
@Singleton
class Scrobbler @Inject constructor(
    private val scrobbleRepository: ScrobbleRepository,
    @ApplicationScope private val scope: CoroutineScope,
) {

    private var currentSongId: String? = null
    private var submitted = false

    fun onTrackStarted(songId: String?) {
        if (songId.isNullOrBlank()) return
        currentSongId = songId
        submitted = false
        scope.launch { runCatching { scrobbleRepository.nowPlaying(songId) } }
    }

    fun onProgress(positionMs: Long, durationMs: Long) {
        val songId = currentSongId ?: return
        if (submitted) return
        if (ScrobbleRules.shouldSubmit(positionMs, durationMs)) {
            submit(songId)
        }
    }

    fun onTrackEnded(songId: String?) {
        val id = songId ?: currentSongId ?: return
        if (submitted) return
        submit(id)
    }

    fun reset() {
        currentSongId = null
        submitted = false
    }

    private fun submit(songId: String) {
        submitted = true
        val playedAt = System.currentTimeMillis()
        scope.launch { runCatching { scrobbleRepository.scrobble(songId, playedAt) } }
    }
}
