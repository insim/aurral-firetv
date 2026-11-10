package org.aurral.firetv.ui.artists

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.aurral.firetv.data.model.Song
import org.aurral.firetv.data.repository.LibraryRepository
import org.aurral.firetv.data.repository.MixRepository
import org.aurral.firetv.playback.PlaybackController
import org.aurral.firetv.ui.common.LoadState
import org.aurral.firetv.ui.common.toUserMessage
import org.aurral.firetv.ui.navigation.Routes
import javax.inject.Inject

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val libraryRepository: LibraryRepository,
    private val mixRepository: MixRepository,
    private val playbackController: PlaybackController,
) : ViewModel() {

    private val artistId: String = savedStateHandle.get<String>(Routes.ARTIST_ID).orEmpty()

    private val _state = MutableStateFlow<LoadState<ArtistDetailUiState>>(LoadState.Loading)
    val state: StateFlow<LoadState<ArtistDetailUiState>> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = LoadState.Loading
            _state.value = runCatching {
                val artist = libraryRepository.artist(artistId)
                val topSongs = runCatching { libraryRepository.topSongs(artistId) }.getOrDefault(emptyList())
                ArtistDetailUiState(artist = artist, topSongs = topSongs)
            }.fold(
                onSuccess = { LoadState.Data(it) },
                onFailure = { LoadState.Error(it.toUserMessage()) },
            )
        }
    }

    fun playFrom(startIndex: Int) {
        val songs = topSongs() ?: return
        playbackController.playSongs(songs, startIndex)
    }

    fun playShuffled() {
        val songs = topSongs() ?: return
        playbackController.playSongs(songs, shuffle = true)
    }

    /** Shuffled artist catalogue, seeded with their most played tracks. */
    fun playRadio() {
        viewModelScope.launch {
            val songs = runCatching { mixRepository.artistRadio(artistId) }.getOrDefault(emptyList())
            if (songs.isNotEmpty()) playbackController.playSongs(songs, shuffle = true)
        }
    }

    private fun topSongs(): List<Song>? =
        (_state.value as? LoadState.Data)?.value?.topSongs?.takeIf { it.isNotEmpty() }
}
