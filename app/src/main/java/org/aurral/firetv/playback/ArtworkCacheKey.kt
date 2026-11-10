package org.aurral.firetv.playback

/** Pixel size requested for the artwork used by the player surfaces. */
const val PLAYBACK_ARTWORK_SIZE = 640

/**
 * Stable Coil cache key for cover art.
 *
 * Independent of the signed URL, so cached artwork survives salt rotation and
 * app restarts instead of being orphaned.
 */
fun artworkCacheKey(coverArtId: String, sizePx: Int): String = "$coverArtId@$sizePx"
