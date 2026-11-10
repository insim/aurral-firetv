package org.aurral.firetv.playback

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.aurral.firetv.data.api.SubsonicUrlBuilder
import org.aurral.firetv.data.model.PlayQueue
import org.aurral.firetv.data.model.Song
import org.aurral.firetv.data.repository.PlayQueueRepository
import org.aurral.firetv.di.qualifiers.MainScope
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The app's single playback facade. Connects a [MediaController] to
 * [PlaybackService], exposes a [StateFlow] of [PlaybackState] to the UI, and
 * keeps the server-side play queue in sync.
 *
 * A Media3 controller may only be touched on the thread that built it, so every
 * access here — including the background ticker and queue autosave — runs on the
 * main thread via the injected [MainScope]. Calls from callers must be on main too.
 */
@Singleton
class PlaybackController @Inject constructor(
    @ApplicationContext private val context: Context,
    private val urlBuilder: SubsonicUrlBuilder,
    private val playQueueRepository: PlayQueueRepository,
    @MainScope private val scope: CoroutineScope,
) {

    private val _state = MutableStateFlow(PlaybackState())
    val state: StateFlow<PlaybackState> = _state.asStateFlow()

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null
    private var playerListener: Player.Listener? = null
    private var progressJob: Job? = null
    private var saveJob: Job? = null

    private var cachedQueue: List<QueueItem> = emptyList()
    private var cachedCurrent: QueueItem? = null
    private var cachedCurrentMediaId: String? = null

    fun connect() {
        if (controllerFuture != null) return
        val token = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val future = MediaController.Builder(context, token).buildAsync()
        controllerFuture = future
        future.addListener(
            {
                val mediaController = runCatching { future.get() }.getOrNull() ?: return@addListener
                controller = mediaController
                val listener = object : Player.Listener {
                    override fun onEvents(player: Player, events: Player.Events) {
                        publish(
                            player = player,
                            rebuildQueue = events.containsAny(
                                Player.EVENT_TIMELINE_CHANGED,
                                Player.EVENT_MEDIA_ITEM_TRANSITION,
                                Player.EVENT_SHUFFLE_MODE_ENABLED_CHANGED,
                                Player.EVENT_REPEAT_MODE_CHANGED,
                            ),
                        )
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        publish(mediaController, rebuildQueue = false)
                        if (!isPlaying) persistQueueDebounced()
                    }
                }
                playerListener = listener
                mediaController.addListener(listener)
                publish(mediaController, rebuildQueue = true)
                startProgressUpdates()
            },
            MoreExecutors.directExecutor(),
        )
    }

    fun release() {
        progressJob?.cancel()
        progressJob = null
        saveJob?.cancel()
        saveJob = null
        playerListener?.let { listener -> controller?.removeListener(listener) }
        controllerFuture?.let { MediaController.releaseFuture(it) }
        controller = null
        controllerFuture = null
        playerListener = null
        cachedQueue = emptyList()
        cachedCurrent = null
        cachedCurrentMediaId = null
        _state.value = PlaybackState()
    }

    // region commands

    fun playSongs(songs: List<Song>, startIndex: Int = 0, shuffle: Boolean = false) {
        val mediaController = controller ?: return
        if (songs.isEmpty()) return
        val items = songs.map { it.toMediaItem(urlBuilder) }
        mediaController.setMediaItems(items, startIndex.coerceIn(0, items.lastIndex), 0L)
        mediaController.shuffleModeEnabled = shuffle
        mediaController.prepare()
        mediaController.play()
        persistQueueDebounced()
    }

    fun restoreQueue(playQueue: PlayQueue, autoPlay: Boolean) {
        val mediaController = controller ?: return
        val items = playQueue.songs.map { it.toMediaItem(urlBuilder) }
        if (items.isEmpty()) return
        val index = items.indexOfFirst { it.mediaId == playQueue.currentSongId }.coerceAtLeast(0)
        mediaController.setMediaItems(items, index, playQueue.positionMs)
        mediaController.prepare()
        if (autoPlay) mediaController.play()
    }

    fun addToQueue(song: Song) {
        controller?.addMediaItem(song.toMediaItem(urlBuilder))
        persistQueueDebounced()
    }

    fun playNext(song: Song) {
        val mediaController = controller ?: return
        val index = if (mediaController.mediaItemCount == 0) 0 else mediaController.currentMediaItemIndex + 1
        mediaController.addMediaItem(index, song.toMediaItem(urlBuilder))
        persistQueueDebounced()
    }

    fun togglePlayPause() {
        val mediaController = controller ?: return
        if (mediaController.isPlaying) mediaController.pause() else mediaController.play()
    }

    fun play() = controller?.play()

    fun pause() {
        controller?.pause()
        persistQueueDebounced()
    }

    fun next() = controller?.seekToNextMediaItem()

    fun previous() = controller?.seekToPreviousMediaItem()

    fun seekTo(positionMs: Long) = controller?.seekTo(positionMs.coerceAtLeast(0L))

    fun seekToQueueIndex(index: Int, positionMs: Long = 0L) = controller?.seekTo(index, positionMs)

    fun setShuffleEnabled(enabled: Boolean) {
        controller?.shuffleModeEnabled = enabled
    }

    fun setRepeatMode(mode: Int) {
        controller?.repeatMode = mode
    }

    fun removeFromQueue(index: Int) {
        val mediaController = controller ?: return
        if (index in 0 until mediaController.mediaItemCount) {
            mediaController.removeMediaItem(index)
            persistQueueDebounced()
        }
    }

    fun moveInQueue(fromIndex: Int, toIndex: Int) {
        val mediaController = controller ?: return
        if (fromIndex in 0 until mediaController.mediaItemCount &&
            toIndex in 0 until mediaController.mediaItemCount
        ) {
            mediaController.moveMediaItem(fromIndex, toIndex)
            persistQueueDebounced()
        }
    }

    // endregion

    fun persistQueueNow() {
        saveJob?.cancel()
        scope.launch { persistQueue() }
    }

    private fun startProgressUpdates() {
        if (progressJob != null) return
        progressJob = scope.launch {
            while (isActive) {
                controller?.let { publish(it, rebuildQueue = false) }
                delay(PROGRESS_INTERVAL_MS)
            }
        }
    }

    /**
     * Publishes a state snapshot. The queue is only rebuilt when the timeline
     * actually changes, so a 500-track queue is not reallocated twice a second.
     */
    private fun publish(player: Player, rebuildQueue: Boolean) {
        val itemCount = player.mediaItemCount
        val queue = if (rebuildQueue || cachedQueue.size != itemCount) {
            (0 until itemCount).map { player.getMediaItemAt(it).toQueueItem() }.also { cachedQueue = it }
        } else {
            cachedQueue
        }

        val currentMediaId = player.currentMediaItem?.mediaId
        val current = if (currentMediaId != cachedCurrentMediaId) {
            player.currentMediaItem?.toQueueItem().also {
                cachedCurrent = it
                cachedCurrentMediaId = currentMediaId
            }
        } else {
            cachedCurrent
        }

        val duration = player.duration
            .takeIf { it != C.TIME_UNSET && it > 0L }
            ?: current?.durationMs
            ?: 0L
        _state.value = PlaybackState(
            isConnected = true,
            isPlaying = player.isPlaying,
            isBuffering = player.playbackState == Player.STATE_BUFFERING,
            isEnded = player.playbackState == Player.STATE_ENDED,
            currentSongId = current?.id,
            title = current?.title.orEmpty(),
            artist = current?.artist.orEmpty(),
            album = current?.album.orEmpty(),
            coverArtId = current?.coverArtId,
            durationMs = duration,
            positionMs = player.currentPosition.coerceAtLeast(0L),
            bufferedMs = player.bufferedPosition.coerceAtLeast(0L),
            shuffleEnabled = player.shuffleModeEnabled,
            repeatMode = player.repeatMode,
            queue = queue,
            currentIndex = player.currentMediaItemIndex,
        )
    }

    private fun persistQueueDebounced() {
        saveJob?.cancel()
        saveJob = scope.launch {
            delay(QUEUE_SAVE_DEBOUNCE_MS)
            persistQueue()
        }
    }

    private suspend fun persistQueue() {
        val mediaController = controller ?: return
        if (mediaController.mediaItemCount == 0) return
        val ids = (0 until mediaController.mediaItemCount).map { mediaController.getMediaItemAt(it).mediaId }
        runCatching {
            playQueueRepository.save(
                songIds = ids,
                currentSongId = mediaController.currentMediaItem?.mediaId,
                positionMs = mediaController.currentPosition,
            )
        }
    }

    private companion object {
        const val PROGRESS_INTERVAL_MS = 500L
        const val QUEUE_SAVE_DEBOUNCE_MS = 1_500L
    }
}
