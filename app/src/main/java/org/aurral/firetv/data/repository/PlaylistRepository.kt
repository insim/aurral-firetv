package org.aurral.firetv.data.repository

import org.aurral.firetv.data.api.SubsonicApi
import org.aurral.firetv.data.api.SubsonicException
import org.aurral.firetv.data.api.orThrow
import org.aurral.firetv.data.mapper.toPlaylist
import org.aurral.firetv.data.mapper.toPlaylists
import org.aurral.firetv.data.model.Playlist
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Playlists and Aurral flows. Flows are visible but read-only; edits on a flow
 * fail with Subsonic error 50, surfaced to the UI as a plain message.
 */
@Singleton
class PlaylistRepository @Inject constructor(
    private val api: SubsonicApi,
) {

    suspend fun playlists(): List<Playlist> =
        api.getPlaylists().orThrow().playlists?.playlist?.toPlaylists().orEmpty()

    suspend fun playlist(id: String): Playlist =
        api.getPlaylist(id).orThrow().playlist?.toPlaylist()
            ?: throw SubsonicException(70, "Playlist not found")

    suspend fun createPlaylist(name: String, songIds: List<String> = emptyList()) {
        api.createPlaylist(name, songIds).orThrow()
    }

    suspend fun renamePlaylist(playlistId: String, name: String) {
        api.updatePlaylist(playlistId = playlistId, name = name).orThrow()
    }

    suspend fun addSong(playlistId: String, songId: String) {
        api.updatePlaylist(playlistId = playlistId, songIdsToAdd = listOf(songId)).orThrow()
    }

    /** Adds several tracks in one call (Aurral accepts repeated `songIdToAdd`). */
    suspend fun addSongs(playlistId: String, songIds: List<String>) {
        if (songIds.isEmpty()) return
        api.updatePlaylist(playlistId = playlistId, songIdsToAdd = songIds).orThrow()
    }

    /** Removes the song at [index] (position within the last loaded playlist). */
    suspend fun removeSongAt(playlistId: String, index: Int) {
        api.updatePlaylist(playlistId = playlistId, songIndexesToRemove = listOf(index)).orThrow()
    }

    suspend fun deletePlaylist(playlistId: String) {
        api.deletePlaylist(playlistId).orThrow()
    }
}
