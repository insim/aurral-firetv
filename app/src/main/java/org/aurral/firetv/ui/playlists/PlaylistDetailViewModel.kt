package org.aurral.firetv.ui.playlists

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.aurral.firetv.data.repository.PlaylistRepository
import org.aurral.firetv.playback.PlaybackController
import org.aurral.firetv.ui.common.LoadState
import org.aurral.firetv.ui.common.toUserMessage
import org.aurral.firetv.ui.navigation.Routes
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val playlistRepository: PlaylistRepository,
    private val playbackController: PlaybackController,
) : ViewModel() {

    private val playlistId: String = savedStateHandle.get<String>(Routes.PLAYLIST_ID).orEmpty()

    private val _state = MutableStateFlow<LoadState<PlaylistDetailUiState>>(LoadState.Loading)
    val state: StateFlow<LoadState<PlaylistDetailUiState>> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = LoadState.Loading
            _state.value = runCatching {
                PlaylistDetailUiState(playlist = playlistRepository.playlist(playlistId))
            }.fold(
                onSuccess = { LoadState.Data(it) },
                onFailure = { LoadState.Error(it.toUserMessage()) },
            )
        }
    }

    fun playFrom(startIndex: Int) {
        val songs = (_state.value as? LoadState.Data)?.value?.playlist?.songs ?: return
        playbackController.playSongs(songs, startIndex)
    }

    fun removeAt(index: Int) {
        viewModelScope.launch {
            runCatching { playlistRepository.removeSongAt(playlistId, index) }.fold(
                onSuccess = { load() },
                onFailure = { error -> setError(error.toUserMessage()) },
            )
        }
    }

    fun rename(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            runCatching { playlistRepository.renamePlaylist(playlistId, name) }.fold(
                onSuccess = { load() },
                onFailure = { error -> setError(error.toUserMessage()) },
            )
        }
    }

    fun delete() {
        viewModelScope.launch {
            runCatching { playlistRepository.deletePlaylist(playlistId) }.fold(
                onSuccess = { markDeleted() },
                onFailure = { error -> setError(error.toUserMessage()) },
            )
        }
    }

    fun clearError() {
        _state.update { current ->
            if (current is LoadState.Data) LoadState.Data(current.value.copy(errorMessage = null)) else current
        }
    }

    private fun setError(message: String) {
        _state.update { current ->
            if (current is LoadState.Data) LoadState.Data(current.value.copy(errorMessage = message)) else current
        }
    }

    private fun markDeleted() {
        _state.update { current ->
            if (current is LoadState.Data) LoadState.Data(current.value.copy(deleted = true)) else current
        }
    }
}
