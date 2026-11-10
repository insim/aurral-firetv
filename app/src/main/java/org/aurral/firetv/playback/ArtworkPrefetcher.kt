package org.aurral.firetv.playback

import android.content.Context
import androidx.media3.common.MediaItem
import coil.Coil
import coil.request.ImageRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/** Warms the image cache for the upcoming track so Now Playing swaps in instantly. */
@Singleton
class ArtworkPrefetcher @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun prefetch(item: MediaItem?) {
        val coverArtId = item?.coverArtId() ?: return
        val artworkUri = item.mediaMetadata.artworkUri ?: return
        val cacheKey = artworkCacheKey(coverArtId, PLAYBACK_ARTWORK_SIZE)

        Coil.imageLoader(context).enqueue(
            ImageRequest.Builder(context)
                .data(artworkUri)
                .memoryCacheKey(cacheKey)
                .diskCacheKey(cacheKey)
                .build(),
        )
    }
}
