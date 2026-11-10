package org.aurral.firetv.data.session

/**
 * How the server should deliver audio.
 *
 * [format] maps to the Subsonic `format` parameter (`"raw"` disables transcoding);
 * [maxBitRate] is in kbps and forces transcoding to MP3 when set.
 */
data class StreamSettings(
    val format: String?,
    val maxBitRate: Int?,
) {
    companion object {
        val DEFAULT = StreamSettings(format = "raw", maxBitRate = null)
    }
}
