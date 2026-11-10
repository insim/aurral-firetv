package org.aurral.firetv.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.aurral.firetv.data.model.AlbumListType
import org.aurral.firetv.data.repository.LibraryRepository
import org.aurral.firetv.data.repository.PlaylistRepository
import org.aurral.firetv.ui.common.LoadState
import org.aurral.firetv.ui.common.toUserMessage
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val libraryRepository: LibraryRepository,
    private val playlistRepository: PlaylistRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<LoadState<HomeUiState>>(LoadState.Loading)
    val state: StateFlow<LoadState<HomeUiState>> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = LoadState.Loading
            _state.value = runCatching {
                coroutineScope {
                    val rails = AlbumListType.HOME_RAILS.map { (type, title) ->
                        async { AlbumRail(title, libraryRepository.albumList(type)) }
                    }.awaitAll().filter { it.albums.isNotEmpty() }

                    val playlists = runCatching { playlistRepository.playlists() }.getOrDefault(emptyList())
                    HomeUiState(rails = rails, playlists = playlists)
                }
            }.fold(
                onSuccess = { LoadState.Data(it) },
                onFailure = { LoadState.Error(it.toUserMessage()) },
            )
        }
    }
}
