package org.aurral.firetv

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import dagger.hilt.android.HiltAndroidApp
import org.aurral.firetv.data.cache.CacheSizing
import java.io.File

@HiltAndroidApp
class AurralTvApp : Application(), ImageLoaderFactory {

    /**
     * Cover art is immutable per id and Aurral sends no cache headers, so the
     * loader caches regardless of headers and is bounded by free disk space.
     */
    override fun newImageLoader(): ImageLoader {
        val appContext = this
        val sizing = CacheSizing(appContext)
        return ImageLoader.Builder(appContext)
            .memoryCache {
                MemoryCache.Builder(appContext)
                    .maxSizePercent(MEMORY_CACHE_PERCENT)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(File(appContext.cacheDir, IMAGE_CACHE_DIR))
                    .maxSizeBytes(sizing.imageCacheBytes())
                    .build()
            }
            .respectCacheHeaders(false)
            .crossfade(true)
            .build()
    }

    private companion object {
        const val MEMORY_CACHE_PERCENT = 0.15
        const val IMAGE_CACHE_DIR = "image_cache"
    }
}
