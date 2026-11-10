package org.aurral.firetv.playback

/**
 * Builds the audio cache keys used by the ExoPlayer cache.
 *
 * Keys are derived from the song and its requested encoding rather than the URL,
 * so a rotating auth salt does not orphan previously cached audio.
 */
object StreamCacheKeys {

    private const val DEFAULT_FORMAT = "raw"
    private const val DEFAULT_MAX_BITRATE = "0"

    fun forStream(songId: String, format: String?, maxBitRate: String?): String =
        "stream:$songId:${format ?: DEFAULT_FORMAT}:${maxBitRate ?: DEFAULT_MAX_BITRATE}"
}
