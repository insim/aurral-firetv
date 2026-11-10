package org.aurral.firetv.ui.navigation

import android.net.Uri

/** Routes for detail and utility screens, with argument builders. */
object Routes {

    const val ARTIST_ID = "artistId"
    const val ALBUM_ID = "albumId"
    const val GENRE_NAME = "genreName"
    const val PLAYLIST_ID = "playlistId"
    const val SONG_ID = "songId"

    const val ARTIST_DETAIL = "artist/{$ARTIST_ID}"
    const val ALBUM_DETAIL = "album/{$ALBUM_ID}"
    const val GENRE_DETAIL = "genre/{$GENRE_NAME}"
    const val PLAYLIST_DETAIL = "playlist/{$PLAYLIST_ID}"
    const val NOW_PLAYING = "now_playing"
    const val QUEUE = "queue"
    const val LYRICS = "lyrics/{$SONG_ID}"

    fun artistDetail(artistId: String) = "artist/${Uri.encode(artistId)}"

    fun albumDetail(albumId: String) = "album/${Uri.encode(albumId)}"

    fun genreDetail(genreName: String) = "genre/${Uri.encode(genreName)}"

    fun playlistDetail(playlistId: String) = "playlist/${Uri.encode(playlistId)}"

    fun lyrics(songId: String) = "lyrics/${Uri.encode(songId)}"
}
