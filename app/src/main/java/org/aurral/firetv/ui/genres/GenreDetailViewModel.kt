package org.aurral.firetv.ui.genres

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.aurral.firetv.data.model.Song
import org.aurral.firetv.data.repository.FavoritesRepository
import org.aurral.firetv.data.repository.LibraryRepository
import org.aurral.firetv.playback.PlaybackController
import org.aurral.firetv.ui.common.LoadState
import org.aurral.firetv.ui.common.toUserMessage
import org.aurral.firetv.ui.navigation.Routes
import javax.inject.Inject

@HiltViewModel
class GenreDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val libraryRepository: LibraryRepository,
    private val favoritesRepository: FavoritesRepository,
    private val playbackController: PlaybackController,
) : ViewModel() {

    val genreName: String = savedStateHandle.get<String>(Routes.GENRE_NAME).orEmpty()

    private val _state = MutableStateFlow<LoadState<List<Song>>>(LoadState.Loading)
    val state: StateFlow<LoadState<List<Song>>> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = LoadState.Loading
            _state.value = runCatching { libraryRepository.songsByGenre(genreName) }.fold(
                onSuccess = { LoadState.Data(it) },
                onFailure = { LoadState.Error(it.toUserMessage()) },
            )
        }
    }

    fun playFrom(startIndex: Int) {
        val songs = currentSongs() ?: return
        playbackController.playSongs(songs, startIndex)
    }

    fun playShuffled() {
        val songs = currentSongs() ?: return
        playbackController.playSongs(songs, shuffle = true)
    }

    private fun currentSongs(): List<Song>? =
        (_state.value as? LoadState.Data)?.value?.takeIf { it.isNotEmpty() }

    fun toggleSongStar(song: Song) {
        viewModelScope.launch {
            val starred = !song.starred
            runCatching {
                if (starred) favoritesRepository.starSong(song.id) else favoritesRepository.unstarSong(song.id)
            }.onSuccess {
                val current = _state.value as? LoadState.Data ?: return@onSuccess
                _state.value = LoadState.Data(
                    current.value.map { if (it.id == song.id) it.copy(starred = starred) else it },
                )
            }
        }
    }
}
