package org.aurral.firetv.data.cache

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single [SimpleCache] instance for streamed audio, bounded by an LRU evictor.
 *
 * Only one instance may exist per directory, hence the singleton and the lazy,
 * lock-protected construction.
 */
@OptIn(UnstableApi::class)
@Singleton
class PlaybackCache @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cacheSizing: CacheSizing,
) {

    private val lock = Any()

    @Volatile
    private var cache: SimpleCache? = null

    fun simpleCache(): SimpleCache = cache ?: synchronized(lock) {
        cache ?: createCache().also { cache = it }
    }

    fun sizeBytes(): Long = simpleCache().cacheSpace

    fun clear() {
        val target = simpleCache()
        target.keys.toList().forEach { key -> target.removeResource(key) }
    }

    private fun createCache(): SimpleCache {
        val directory = File(context.cacheDir, DIRECTORY_NAME)
        return SimpleCache(
            directory,
            LeastRecentlyUsedCacheEvictor(cacheSizing.mediaCacheBytes()),
            StandaloneDatabaseProvider(context),
        )
    }

    private companion object {
        const val DIRECTORY_NAME = "media_cache"
    }
}
