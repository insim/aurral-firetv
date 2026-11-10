package org.aurral.firetv.data.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.aurral.firetv.data.model.AlbumListType
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Enumerates the whole library's tracks.
 *
 * Subsonic has no "list every song" endpoint, so this walks the album list and
 * collects each album's tracks. Callers page through it a fixed number of albums
 * at a time rather than pulling the entire library in one go.
 */
@Singleton
class AllSongsRepository @Inject constructor(
    private val libraryRepository: LibraryRepository,
) {

    suspend fun loadPage(albumOffset: Int, albumCount: Int): AllSongsPage = coroutineScope {
        val albums = libraryRepository.albumList(
            type = AlbumListType.ALPHABETICAL_BY_NAME,
            size = albumCount,
            offset = albumOffset,
        )

        val songs = albums
            .map { album ->
                async { runCatching { libraryRepository.album(album.id).songs }.getOrDefault(emptyList()) }
            }
            .awaitAll()
            .flatten()

        AllSongsPage(songs = songs, albumCount = albums.size)
    }
}
