package org.aurral.firetv.data.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.aurral.firetv.data.model.Album
import org.aurral.firetv.data.model.AlbumListType
import org.aurral.firetv.data.model.Song
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Builds playable queues from the library. Every mix is assembled from endpoints
 * that answer from Aurral's own index, so no mix depends on an external service.
 */
@Singleton
class MixRepository @Inject constructor(
    private val libraryRepository: LibraryRepository,
    private val favoritesRepository: FavoritesRepository,
) {

    /** A broad shuffle of whatever the library happens to contain. */
    suspend fun shuffledLibrary(albumCount: Int = 10): List<Song> =
        songsOf(libraryRepository.albumList(AlbumListType.RANDOM, albumCount)).shuffled()

    /** Recently added, random and your favourites, blended and de-duplicated. */
    suspend fun blended(albumCount: Int = 5): List<Song> = coroutineScope {
        val recent = async { songsOf(libraryRepository.albumList(AlbumListType.NEWEST, albumCount)) }
        val random = async { songsOf(libraryRepository.albumList(AlbumListType.RANDOM, albumCount)) }
        val favourites = async { runCatching { favoritesRepository.starred().songs }.getOrDefault(emptyList()) }

        (recent.await() + random.await() + favourites.await())
            .distinctBy { it.id }
            .shuffled()
    }

    /** An artist's own catalogue, shuffled, led by their most played tracks. */
    suspend fun artistRadio(artistId: String): List<Song> = coroutineScope {
        val topSongs = async { runCatching { libraryRepository.topSongs(artistId) }.getOrDefault(emptyList()) }
        val albums = async { runCatching { libraryRepository.artist(artistId).albums }.getOrDefault(emptyList()) }

        val albumSongs = songsOf(albums.await())
        (topSongs.await() + albumSongs)
            .distinctBy { it.id }
            .shuffled()
    }

    private suspend fun songsOf(albums: List<Album>): List<Song> = coroutineScope {
        albums
            .map { album -> async { runCatching { libraryRepository.album(album.id).songs }.getOrDefault(emptyList()) } }
            .awaitAll()
            .flatten()
    }
}
