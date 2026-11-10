package org.aurral.firetv.data.mapper

import org.aurral.firetv.data.api.dto.ChildDto
import org.aurral.firetv.data.model.Song

fun ChildDto.toSong(): Song = Song(
    id = id,
    title = title ?: "Unknown title",
    artist = artist ?: "Unknown artist",
    album = album ?: "",
    albumId = albumId,
    artistId = artistId,
    coverArtId = coverArt,
    durationSeconds = duration ?: 0,
    trackNumber = track,
    discNumber = discNumber,
    year = year,
    genre = genre,
    starred = starred != null,
    userRating = userRating,
    suffix = suffix,
    bitRate = bitRate,
)
