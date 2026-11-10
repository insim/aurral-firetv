package org.aurral.firetv.data.mapper

import org.aurral.firetv.data.api.dto.AlbumDto
import org.aurral.firetv.data.model.Album

fun AlbumDto.toAlbum(): Album = Album(
    id = id,
    name = name ?: title ?: "Unknown album",
    artist = artist ?: "Unknown artist",
    artistId = artistId,
    coverArtId = coverArt,
    songCount = songCount ?: song.size,
    durationSeconds = duration ?: 0,
    year = year,
    genre = genre,
    starred = starred != null,
    userRating = userRating,
    songs = song.toSongs(),
)

fun List<AlbumDto>.toAlbums(): List<Album> = map { it.toAlbum() }
