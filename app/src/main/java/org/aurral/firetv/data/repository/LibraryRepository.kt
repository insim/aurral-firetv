package org.aurral.firetv.data.repository

import org.aurral.firetv.data.api.SubsonicApi
import org.aurral.firetv.data.api.SubsonicException
import org.aurral.firetv.data.api.orThrow
import org.aurral.firetv.data.mapper.toAlbum
import org.aurral.firetv.data.mapper.toAlbums
import org.aurral.firetv.data.mapper.toArtist
import org.aurral.firetv.data.mapper.toArtistBio
import org.aurral.firetv.data.mapper.toArtists
import org.aurral.firetv.data.mapper.toGenres
import org.aurral.firetv.data.mapper.toSongs
import org.aurral.firetv.data.model.Album
import org.aurral.firetv.data.model.AlbumListType
import org.aurral.firetv.data.model.Artist
import org.aurral.firetv.data.model.ArtistBio
import org.aurral.firetv.data.model.Genre
import org.aurral.firetv.data.model.Song
import javax.inject.Inject
import javax.inject.Singleton

/** Read-only browsing of the Aurral library. */
@Singleton
class LibraryRepository @Inject constructor(
    private val api: SubsonicApi,
) {

    suspend fun albumList(type: AlbumListType, size: Int = 50, offset: Int = 0): List<Album> =
        api.getAlbumList2(type.apiValue, size, offset)
            .orThrow()
            .albumList2
            ?.album
            ?.toAlbums()
            .orEmpty()

    suspend fun artists(): List<Artist> =
        api.getArtists()
            .orThrow()
            .artists
            ?.index
            ?.flatMap { it.artist }
            ?.toArtists()
            .orEmpty()

    suspend fun artist(id: String): Artist =
        api.getArtist(id).orThrow().artist?.toArtist()
            ?: throw SubsonicException(70, "Artist not found")

    suspend fun album(id: String): Album =
        api.getAlbum(id).orThrow().album?.toAlbum()
            ?: throw SubsonicException(70, "Album not found")

    /** Biography for an artist; null when the server has none or does not support it. */
    suspend fun artistBio(artistId: String): ArtistBio? =
        api.getArtistInfo2(artistId).orThrow().artistInfo2?.toArtistBio()

    suspend fun topSongs(artistId: String, count: Int = 20): List<Song> =
        api.getTopSongs(artistId, count).orThrow().topSongs?.song?.toSongs().orEmpty()

    suspend fun genres(): List<Genre> =
        api.getGenres().orThrow().genres?.genre?.toGenres().orEmpty()

    suspend fun songsByGenre(genre: String): List<Song> =
        api.getSongsByGenre(genre).orThrow().songsByGenre?.song?.toSongs().orEmpty()
}
