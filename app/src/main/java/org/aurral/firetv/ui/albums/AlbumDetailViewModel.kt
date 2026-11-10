package org.aurral.firetv.ui.albums

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.aurral.firetv.data.model.Playlist
import org.aurral.firetv.data.model.Song
import org.aurral.firetv.data.repository.FavoritesRepository
import org.aurral.firetv.data.repository.LibraryRepository
import org.aurral.firetv.data.repository.PlaylistRepository
import org.aurral.firetv.playback.PlaybackController
import org.aurral.firetv.ui.common.LoadState
import org.aurral.firetv.ui.common.toUserMessage
import org.aurral.firetv.ui.navigation.Routes
import javax.inject.Inject

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val libraryRepository: LibraryRepository,
    private val favoritesRepository: FavoritesRepository,
    private val playlistRepository: PlaylistRepository,
    private val playbackController: PlaybackController,
) : ViewModel() {

    private val albumId: String = savedStateHandle.get<String>(Routes.ALBUM_ID).orEmpty()

    private val _state = MutableStateFlow<LoadState<AlbumDetailUiState>>(LoadState.Loading)
    val state: StateFlow<LoadState<AlbumDetailUiState>> = _state.asStateFlow()

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = LoadState.Loading
            _state.value = runCatching { AlbumDetailUiState(libraryRepository.album(albumId)) }.fold(
                onSuccess = { LoadState.Data(it) },
                onFailure = { LoadState.Error(it.toUserMessage()) },
            )
        }
    }

    fun playFrom(startIndex: Int) {
        val songs = (_state.value as? LoadState.Data)?.value?.album?.songs ?: return
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

    fun loadPlaylists() {
        viewModelScope.launch {
            runCatching { playlistRepository.playlists() }.onSuccess { _playlists.value = it }
        }
    }

    fun addAlbumToPlaylist(playlistId: String) {
        val songIds = currentSongIds() ?: return
        viewModelScope.launch {
            runCatching { playlistRepository.addSongs(playlistId, songIds) }
        }
    }

    fun createPlaylistWithAlbum(name: String) {
        val songIds = currentSongIds() ?: return
        viewModelScope.launch {
            runCatching { playlistRepository.createPlaylist(name, songIds) }
            loadPlaylists()
        }
    }

    private fun currentSongIds(): List<String>? =
        (_state.value as? LoadState.Data)?.value?.album?.songs?.map { it.id }

    private fun applyStar(songId: String, starred: Boolean) {
        val current = _state.value as? LoadState.Data ?: return
        val album = current.value.album
        val updated = album.copy(
            songs = album.songs.map { if (it.id == songId) it.copy(starred = starred) else it },
        )
        _state.value = LoadState.Data(current.value.copy(album = updated))
    }
}
