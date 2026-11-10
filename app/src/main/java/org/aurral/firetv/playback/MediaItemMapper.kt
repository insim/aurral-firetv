package org.aurral.firetv.playback

import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import org.aurral.firetv.data.api.SubsonicUrlBuilder
import org.aurral.firetv.data.model.Song

private const val EXTRA_COVER_ART_ID = "coverArtId"

/** The cover-art id carried by a queue item, used for cache keys and prefetching. */
fun MediaItem.coverArtId(): String? = mediaMetadata.extras?.getString(EXTRA_COVER_ART_ID)

/** Builds a playable [MediaItem] (stream URL + now-playing metadata) from a song. */
fun Song.toMediaItem(urlBuilder: SubsonicUrlBuilder): MediaItem {
    val artworkUri = coverArtId
        ?.takeIf { it.isNotBlank() }
        ?.let { urlBuilder.coverArtUrl(it, size = PLAYBACK_ARTWORK_SIZE) }
        ?.toUri()

    val metadata = MediaMetadata.Builder()
        .setTitle(title)
        .setArtist(artist)
        .setAlbumTitle(album)
        .setDurationMs(durationSeconds * 1000L)
        .setArtworkUri(artworkUri)
        .setIsBrowsable(false)
        .setIsPlayable(true)
        .setExtras(bundleOf(EXTRA_COVER_ART_ID to coverArtId))
        .build()

    return MediaItem.Builder()
        .setMediaId(id)
        .setUri(urlBuilder.streamUrl(id).orEmpty())
        .setMediaMetadata(metadata)
        .build()
}

/** Reconstructs a [QueueItem] from a [MediaItem] for display in the queue. */
fun MediaItem.toQueueItem(): QueueItem {
    val metadata = mediaMetadata
    return QueueItem(
        id = mediaId,
        title = metadata.title?.toString().orEmpty(),
        artist = metadata.artist?.toString().orEmpty(),
        album = metadata.albumTitle?.toString().orEmpty(),
        coverArtId = coverArtId(),
        durationMs = metadata.durationMs.takeIf { it > 0L } ?: 0L,
    )
}
