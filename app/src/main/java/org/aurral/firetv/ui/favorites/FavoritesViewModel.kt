package org.aurral.firetv.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.aurral.firetv.data.model.Song
import org.aurral.firetv.data.model.StarredCollection
import org.aurral.firetv.data.repository.FavoritesRepository
import org.aurral.firetv.playback.PlaybackController
import org.aurral.firetv.ui.common.LoadState
import org.aurral.firetv.ui.common.toUserMessage
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val playbackController: PlaybackController,
) : ViewModel() {

    private val _state = MutableStateFlow<LoadState<StarredCollection>>(LoadState.Loading)
    val state: StateFlow<LoadState<StarredCollection>> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = LoadState.Loading
            _state.value = runCatching { favoritesRepository.starred() }.fold(
                onSuccess = { LoadState.Data(it) },
                onFailure = { LoadState.Error(it.toUserMessage()) },
            )
        }
    }

    fun playFrom(startIndex: Int) {
        val songs = (_state.value as? LoadState.Data)?.value?.songs ?: return
        playbackController.playSongs(songs, startIndex)
    }

    fun toggleSongStar(song: Song) {
        viewModelScope.launch {
            val starred = !song.starred
            runCatching {
                if (starred) favoritesRepository.starSong(song.id) else favoritesRepository.unstarSong(song.id)
            }.onSuccess {
                applyStar(song.id, starred)
            }
        }
    }

    private fun applyStar(songId: String, starred: Boolean) {
        val current = _state.value as? LoadState.Data ?: return
        val collection = current.value
        val songs = if (starred) {
            collection.songs.map { if (it.id == songId) it.copy(starred = true) else it }
        } else {
            collection.songs
                .filterNot { it.id == songId }
                .map { if (it.id == songId) it.copy(starred = false) else it }
        }
        _state.value = LoadState.Data(collection.copy(songs = songs))
    }
}
