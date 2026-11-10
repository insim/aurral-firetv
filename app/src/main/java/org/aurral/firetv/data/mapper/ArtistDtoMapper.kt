package org.aurral.firetv.data.mapper

import org.aurral.firetv.data.api.dto.ArtistDto
import org.aurral.firetv.data.model.Artist

fun ArtistDto.toArtist(): Artist = Artist(
    id = id,
    name = name ?: "Unknown artist",
    albumCount = albumCount ?: album.size,
    coverArtId = coverArt,
    starred = starred != null,
    albums = album.toAlbums(),
)

fun List<ArtistDto>.toArtists(): List<Artist> = map { it.toArtist() }
