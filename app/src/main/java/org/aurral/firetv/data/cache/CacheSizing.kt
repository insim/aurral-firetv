package org.aurral.firetv.data.cache

import android.content.Context
import android.os.StatFs
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Derives cache budgets from the free space on the device, so the app never
 * claims a large fixed slice of a Fire TV Stick's limited storage. Budgets are
 * a fraction of free space, floored so the cache stays useful and capped so it
 * can never balloon.
 */
class CacheSizing @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun imageCacheBytes(): Long = clamp(availableBytes() / 48, MIN_IMAGE_BYTES, MAX_IMAGE_BYTES)

    fun mediaCacheBytes(): Long = clamp(availableBytes() / 12, MIN_MEDIA_BYTES, MAX_MEDIA_BYTES)

    private fun availableBytes(): Long = runCatching {
        StatFs(context.cacheDir.absolutePath).availableBytes
    }.getOrDefault(0L)

    private fun clamp(value: Long, min: Long, max: Long): Long = value.coerceIn(min, max)

    private companion object {
        const val KIB = 1024L
        const val MIB = KIB * 1024

        const val MIN_IMAGE_BYTES = 32L * MIB
        const val MAX_IMAGE_BYTES = 128L * MIB
        const val MIN_MEDIA_BYTES = 128L * MIB
        const val MAX_MEDIA_BYTES = 384L * MIB
    }
}
