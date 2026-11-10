package org.aurral.firetv.data.cache

/** Bytes currently held by each cache, for display in Settings. */
data class CacheSizes(
    val imageBytes: Long,
    val mediaBytes: Long,
) {
    val totalBytes: Long get() = imageBytes + mediaBytes
}
