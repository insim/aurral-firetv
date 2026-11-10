package org.aurral.firetv.data.repository

import org.aurral.firetv.data.api.SubsonicApi
import org.aurral.firetv.data.api.orThrow
import org.aurral.firetv.data.mapper.toStarredCollection
import org.aurral.firetv.data.model.SearchResults
import org.aurral.firetv.data.model.StarredCollection
import javax.inject.Inject
import javax.inject.Singleton

/** Starring (favorites) and ratings, which Aurral stores per user. */
@Singleton
class FavoritesRepository @Inject constructor(
    private val api: SubsonicApi,
) {

    suspend fun starred(): StarredCollection =
        api.getStarred2().orThrow().starred2?.toStarredCollection()
            ?: StarredCollection(emptyList(), emptyList(), emptyList())

    suspend fun starSong(songId: String) = api.star(id = songId).orThrow()

    suspend fun unstarSong(songId: String) = api.unstar(id = songId).orThrow()

    suspend fun starAlbum(albumId: String) = api.star(albumId = albumId).orThrow()

    suspend fun unstarAlbum(albumId: String) = api.unstar(albumId = albumId).orThrow()

    suspend fun starArtist(artistId: String) = api.star(artistId = artistId).orThrow()

    suspend fun unstarArtist(artistId: String) = api.unstar(artistId = artistId).orThrow()

    suspend fun setRating(songId: String, rating: Int) = api.setRating(songId, rating).orThrow()
}
