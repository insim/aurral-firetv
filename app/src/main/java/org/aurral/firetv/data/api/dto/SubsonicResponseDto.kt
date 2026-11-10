package org.aurral.firetv.data.api.dto

import kotlinx.serialization.Serializable

/**
 * The body of a Subsonic response. Every endpoint returns one of these; unused
 * fields are simply null. Unknown fields are ignored by the JSON parser.
 */
@Serializable
data class SubsonicResponseDto(
    val status: String = "ok",
    val version: String? = null,
    val type: String? = null,
    val serverVersion: String? = null,
    val openSubsonic: Boolean = false,
    val error: ErrorDto? = null,
    val album: AlbumDto? = null,
    val albumList2: AlbumListDto? = null,
    val artists: ArtistsDto? = null,
    val artist: ArtistDto? = null,
    val genres: GenresDto? = null,
    val songsByGenre: SongsDto? = null,
    val searchResult3: SearchResult3Dto? = null,
    val starred2: Starred2Dto? = null,
    val topSongs: SongsDto? = null,
    val playlists: PlaylistsDto? = null,
    val playlist: PlaylistDto? = null,
    val lyricsList: LyricsListDto? = null,
    val playQueue: PlayQueueDto? = null,
)
