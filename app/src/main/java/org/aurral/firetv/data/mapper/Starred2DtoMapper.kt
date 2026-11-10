package org.aurral.firetv.data.mapper

import org.aurral.firetv.data.api.dto.Starred2Dto
import org.aurral.firetv.data.model.StarredCollection

fun Starred2Dto.toStarredCollection(): StarredCollection = StarredCollection(
    artists = artist.toArtists(),
    albums = album.toAlbums(),
    songs = song.toSongs(),
)
