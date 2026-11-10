package org.aurral.firetv.playback

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.cache.CacheKeyFactory

/**
 * Keys the audio cache through [StreamCacheKeys] rather than the full URL, so
 * entries survive the salt rotating between sessions instead of becoming
 * unreachable dead weight.
 *
 * The key must identify the whole resource and therefore never includes the
 * requested byte range.
 */
@OptIn(UnstableApi::class)
class SubsonicCacheKeyFactory : CacheKeyFactory {

    override fun buildCacheKey(dataSpec: DataSpec): String {
        val uri = dataSpec.uri
        val songId = uri.getQueryParameter(QUERY_ID)
        if (songId.isNullOrBlank()) return dataSpec.key ?: uri.toString()

        return StreamCacheKeys.forStream(
            songId = songId,
            format = uri.getQueryParameter(QUERY_FORMAT),
            maxBitRate = uri.getQueryParameter(QUERY_MAX_BITRATE),
        )
    }

    private companion object {
        const val QUERY_ID = "id"
        const val QUERY_FORMAT = "format"
        const val QUERY_MAX_BITRATE = "maxBitRate"
    }
}
