package org.aurral.firetv.ui.player

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import org.aurral.firetv.playback.PlaybackController
import org.aurral.firetv.playback.PlaybackState
import javax.inject.Inject

/** Thin, UI-facing wrapper over the shared [PlaybackController]. */
@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playbackController: PlaybackController,
) : ViewModel() {

    val state: StateFlow<PlaybackState> = playbackController.state

    fun togglePlayPause() = playbackController.togglePlayPause()
    fun next() = playbackController.next()
    fun previous() = playbackController.previous()
    fun seekTo(positionMs: Long) = playbackController.seekTo(positionMs)
    fun seekToQueueIndex(index: Int) = playbackController.seekToQueueIndex(index)
    fun setShuffleEnabled(enabled: Boolean) = playbackController.setShuffleEnabled(enabled)
    fun setRepeatMode(mode: Int) = playbackController.setRepeatMode(mode)
    fun removeFromQueue(index: Int) = playbackController.removeFromQueue(index)
    fun moveInQueue(fromIndex: Int, toIndex: Int) = playbackController.moveInQueue(fromIndex, toIndex)
    fun persistQueue() = playbackController.persistQueueNow()
}
