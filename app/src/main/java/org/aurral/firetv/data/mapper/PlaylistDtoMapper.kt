package org.aurral.firetv.data.mapper

import org.aurral.firetv.data.api.dto.PlaylistDto
import org.aurral.firetv.data.model.Playlist

fun PlaylistDto.toPlaylist(): Playlist = Playlist(
    id = id,
    name = name ?: "Untitled playlist",
    owner = owner,
    comment = comment,
    songCount = songCount ?: entry.size,
    durationSeconds = duration ?: 0,
    coverArtId = coverArt,
    songs = entry.toSongs(),
)

fun List<PlaylistDto>.toPlaylists(): List<Playlist> = map { it.toPlaylist() }
