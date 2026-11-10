package org.aurral.firetv.data.cache

import android.content.Context
import coil.Coil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/** Reports and clears the app's on-disk caches (cover art and streamed audio). */
@Singleton
class CacheManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val playbackCache: PlaybackCache,
) {

    suspend fun sizes(): CacheSizes = withContext(Dispatchers.IO) {
        CacheSizes(
            imageBytes = Coil.imageLoader(context).diskCache?.size ?: 0L,
            mediaBytes = runCatching { playbackCache.sizeBytes() }.getOrDefault(0L),
        )
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) {
            Coil.imageLoader(context).let { loader ->
                loader.diskCache?.clear()
                loader.memoryCache?.clear()
            }
            runCatching { playbackCache.clear() }
            Unit
        }
    }
}
