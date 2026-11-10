package org.aurral.firetv.ui.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.aurral.firetv.data.model.Playlist
import org.aurral.firetv.data.model.PlaylistSort
import org.aurral.firetv.data.model.orderedBy
import org.aurral.firetv.data.repository.PlaylistRepository
import org.aurral.firetv.ui.common.LoadState
import org.aurral.firetv.ui.common.toUserMessage
import javax.inject.Inject

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<LoadState<List<Playlist>>>(LoadState.Loading)
    val state: StateFlow<LoadState<List<Playlist>>> = _state.asStateFlow()

    private val _sort = MutableStateFlow(PlaylistSort.NAME)
    val sort: StateFlow<PlaylistSort> = _sort.asStateFlow()

    private var playlists: List<Playlist> = emptyList()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = LoadState.Loading
            runCatching { playlistRepository.playlists() }.fold(
                onSuccess = { loaded ->
                    playlists = loaded
                    _state.value = LoadState.Data(loaded.orderedBy(_sort.value))
                },
                onFailure = { _state.value = LoadState.Error(it.toUserMessage()) },
            )
        }
    }

    fun setSort(sort: PlaylistSort) {
        _sort.value = sort
        if (playlists.isNotEmpty()) {
            _state.value = LoadState.Data(playlists.orderedBy(sort))
        }
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            runCatching { playlistRepository.createPlaylist(name) }.onSuccess { load() }
        }
    }
}
