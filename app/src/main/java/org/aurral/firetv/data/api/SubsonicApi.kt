package org.aurral.firetv.data.api

import org.aurral.firetv.data.api.dto.SubsonicEnvelope
import org.aurral.firetv.data.api.dto.SubsonicResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Subsonic REST endpoints as served by Aurral at `/{rest}`.
 *
 * Auth and the global `u/v/c/f` parameters are injected by
 * [org.aurral.firetv.data.api.interceptor.SubsonicRequestInterceptor], so methods here only
 * carry endpoint-specific arguments.
 */
interface SubsonicApi {

    @GET("ping.view")
    suspend fun ping(): SubsonicEnvelope

    @GET("getAlbumList2.view")
    suspend fun getAlbumList2(
        @Query("type") type: String,
        @Query("size") size: Int = 50,
        @Query("offset") offset: Int = 0,
    ): SubsonicEnvelope

    @GET("getArtists.view")
    suspend fun getArtists(): SubsonicEnvelope

    @GET("getArtist.view")
    suspend fun getArtist(@Query("id") id: String): SubsonicEnvelope

    @GET("getAlbum.view")
    suspend fun getAlbum(@Query("id") id: String): SubsonicEnvelope

    @GET("getTopSongs.view")
    suspend fun getTopSongs(
        @Query("id") id: String,
        @Query("count") count: Int = 20,
    ): SubsonicEnvelope

    @GET("getGenres.view")
    suspend fun getGenres(): SubsonicEnvelope

    @GET("getSongsByGenre.view")
    suspend fun getSongsByGenre(
        @Query("genre") genre: String,
        @Query("count") count: Int = 200,
        @Query("offset") offset: Int = 0,
    ): SubsonicEnvelope

    @GET("search3.view")
    suspend fun search3(
        @Query("query") query: String,
        @Query("artistCount") artistCount: Int = 20,
        @Query("albumCount") albumCount: Int = 20,
        @Query("songCount") songCount: Int = 50,
    ): SubsonicEnvelope

    @GET("getStarred2.view")
    suspend fun getStarred2(): SubsonicEnvelope

    @GET("star.view")
    suspend fun star(
        @Query("id") id: String? = null,
        @Query("albumId") albumId: String? = null,
        @Query("artistId") artistId: String? = null,
    ): SubsonicEnvelope

    @GET("unstar.view")
    suspend fun unstar(
        @Query("id") id: String? = null,
        @Query("albumId") albumId: String? = null,
        @Query("artistId") artistId: String? = null,
    ): SubsonicEnvelope

    @GET("setRating.view")
    suspend fun setRating(
        @Query("id") id: String,
        @Query("rating") rating: Int,
    ): SubsonicEnvelope

    @GET("getPlaylists.view")
    suspend fun getPlaylists(): SubsonicEnvelope

    @GET("getPlaylist.view")
    suspend fun getPlaylist(@Query("id") id: String): SubsonicEnvelope

    @GET("createPlaylist.view")
    suspend fun createPlaylist(
        @Query("name") name: String,
        @Query("songId") songIds: List<String> = emptyList(),
    ): SubsonicEnvelope

    /** Replaces an existing playlist's contents, which is how Subsonic expresses reordering. */
    @GET("createPlaylist.view")
    suspend fun replacePlaylistSongs(
        @Query("playlistId") playlistId: String,
        @Query("songId") songIds: List<String>,
    ): SubsonicEnvelope

    @GET("updatePlaylist.view")
    suspend fun updatePlaylist(
        @Query("playlistId") playlistId: String,
        @Query("name") name: String? = null,
        @Query("songIdToAdd") songIdsToAdd: List<String> = emptyList(),
        @Query("songIndexToRemove") songIndexesToRemove: List<Int> = emptyList(),
    ): SubsonicEnvelope

    @GET("deletePlaylist.view")
    suspend fun deletePlaylist(@Query("id") id: String): SubsonicEnvelope

    @GET("getLyricsBySongId.view")
    suspend fun getLyricsBySongId(@Query("id") id: String): SubsonicEnvelope

    @GET("scrobble.view")
    suspend fun scrobble(
        @Query("id") id: String,
        @Query("time") time: Long? = null,
        @Query("submission") submission: Boolean = true,
    ): SubsonicEnvelope

    @GET("getPlayQueue.view")
    suspend fun getPlayQueue(): SubsonicEnvelope

    @GET("savePlayQueue.view")
    suspend fun savePlayQueue(
        @Query("id") ids: List<String>,
        @Query("current") current: String? = null,
        @Query("position") position: Long? = null,
    ): SubsonicEnvelope
}
