package org.aurral.firetv.data.model

data class SearchResults(
    val artists: List<Artist>,
    val albums: List<Album>,
    val songs: List<Song>,
) {
    val isEmpty: Boolean get() = artists.isEmpty() && albums.isEmpty() && songs.isEmpty()
}
