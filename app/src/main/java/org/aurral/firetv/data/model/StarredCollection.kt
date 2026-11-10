package org.aurral.firetv.data.model

/** The current user's starred artists, albums and songs. */
data class StarredCollection(
    val artists: List<Artist>,
    val albums: List<Album>,
    val songs: List<Song>,
) {
    val isEmpty: Boolean get() = artists.isEmpty() && albums.isEmpty() && songs.isEmpty()
}
