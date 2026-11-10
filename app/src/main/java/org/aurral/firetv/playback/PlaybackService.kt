package org.aurral.firetv.playback

import android.app.PendingIntent
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.LoadControl
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint
import org.aurral.firetv.MainActivity
import org.aurral.firetv.data.cache.PlaybackCache
import javax.inject.Inject

/**
 * Background playback host. Owns the [ExoPlayer] and the [MediaSession], keeps a
 * disk cache of streamed audio, and reports playback progress to the [Scrobbler].
 */
@OptIn(UnstableApi::class)
@AndroidEntryPoint
class PlaybackService : MediaSessionService() {

    @Inject
    lateinit var scrobbler: Scrobbler

    @Inject
    lateinit var playbackCache: PlaybackCache

    @Inject
    lateinit var artworkPrefetcher: ArtworkPrefetcher

    private lateinit var player: ExoPlayer
    private var mediaSession: MediaSession? = null

    private val handler = Handler(Looper.getMainLooper())
    private val progressTicker = object : Runnable {
        override fun run() {
            if (player.playbackState == Player.STATE_READY) {
                scrobbler.onProgress(player.currentPosition, player.durationOrZero())
            }
            handler.postDelayed(this, PROGRESS_TICK_MS)
        }
    }

    private val playerListener = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            scrobbler.onTrackStarted(mediaItem?.mediaId)
            artworkPrefetcher.prefetch(player.nextArtworkItem())
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_ENDED) {
                scrobbler.onTrackEnded(player.currentMediaItem?.mediaId)
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (!isPlaying) {
                scrobbler.onProgress(player.currentPosition, player.durationOrZero())
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        val cachedDataSource = CacheDataSource.Factory()
            .setCache(playbackCache.simpleCache())
            .setUpstreamDataSourceFactory(DefaultDataSource.Factory(this))
            .setCacheKeyFactory(SubsonicCacheKeyFactory())
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

        player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(this).setDataSourceFactory(cachedDataSource),
            )
            .setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus = */ true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .setLoadControl(buildLoadControl())
            .build()
        player.addListener(playerListener)

        val sessionActivity = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(sessionActivity)
            .build()

        handler.postDelayed(progressTicker, PROGRESS_TICK_MS)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onTaskRemoved(rootIntent: Intent?) {
        if (!player.playWhenReady || player.mediaItemCount == 0) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        handler.removeCallbacks(progressTicker)
        player.removeListener(playerListener)
        mediaSession?.release()
        player.release()
        scrobbler.reset()
        super.onDestroy()
    }

    /** Generous forward buffer for low-bitrate audio, plus a back buffer so "previous" is instant. */
    private fun buildLoadControl(): LoadControl = DefaultLoadControl.Builder()
        .setBufferDurationsMs(
            MIN_BUFFER_MS,
            MAX_BUFFER_MS,
            BUFFER_FOR_PLAYBACK_MS,
            BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS,
        )
        .setBackBuffer(BACK_BUFFER_MS, true)
        .setPrioritizeTimeOverSizeThresholds(true)
        .build()

    private fun Player.durationOrZero(): Long = duration.let { if (it == C.TIME_UNSET) 0L else it }

    /** The upcoming item, or null at the end of the queue. */
    private fun Player.nextArtworkItem(): MediaItem? {
        val nextIndex = nextMediaItemIndex
        return if (nextIndex == C.INDEX_UNSET) null else getMediaItemAt(nextIndex)
    }

    private companion object {
        const val PROGRESS_TICK_MS = 5_000L
        const val MIN_BUFFER_MS = 20_000
        const val MAX_BUFFER_MS = 120_000
        const val BUFFER_FOR_PLAYBACK_MS = 1_000
        const val BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS = 3_000
        const val BACK_BUFFER_MS = 60_000
    }
}
