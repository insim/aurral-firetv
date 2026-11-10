package org.aurral.firetv.data.repository

import org.aurral.firetv.data.api.SubsonicApi
import org.aurral.firetv.data.api.orThrow
import org.aurral.firetv.data.mapper.toPlayQueue
import org.aurral.firetv.data.model.PlayQueue
import javax.inject.Inject
import javax.inject.Singleton

/** Personal play queue, so playback can resume on another client or after a restart. */
@Singleton
class PlayQueueRepository @Inject constructor(
    private val api: SubsonicApi,
) {

    suspend fun load(): PlayQueue? =
        api.getPlayQueue().orThrow().playQueue?.toPlayQueue()?.takeUnless { it.isEmpty }

    suspend fun save(songIds: List<String>, currentSongId: String?, positionMs: Long) {
        api.savePlayQueue(
            ids = songIds,
            current = currentSongId,
            position = positionMs,
        ).orThrow()
    }

    suspend fun clear() {
        api.savePlayQueue(ids = emptyList()).orThrow()
    }
}
