package org.aurral.firetv.data.mapper

import org.aurral.firetv.data.api.dto.SearchResult3Dto
import org.aurral.firetv.data.model.SearchResults

fun SearchResult3Dto.toSearchResults(): SearchResults = SearchResults(
    artists = artist.toArtists(),
    albums = album.toAlbums(),
    songs = song.toSongs(),
)
