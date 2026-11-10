package org.aurral.firetv.ui.songs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.aurral.firetv.data.model.Song
import org.aurral.firetv.data.model.SongSort
import org.aurral.firetv.data.model.orderedBy
import org.aurral.firetv.data.repository.AllSongsRepository
import org.aurral.firetv.playback.PlaybackController
import org.aurral.firetv.ui.common.LoadState
import org.aurral.firetv.ui.common.toUserMessage
import javax.inject.Inject

/**
 * The whole library's tracks, loaded album by album. Sorting is applied to what
 * has been loaded so far; the list keeps growing as you scroll.
 */
@HiltViewModel
class SongsViewModel @Inject constructor(
    private val allSongsRepository: AllSongsRepository,
    private val playbackController: PlaybackController,
) : ViewModel() {

    private val _state = MutableStateFlow<LoadState<List<Song>>>(LoadState.Loading)
    val state: StateFlow<LoadState<List<Song>>> = _state.asStateFlow()

    private val _sort = MutableStateFlow(SongSort.TITLE)
    val sort: StateFlow<SongSort> = _sort.asStateFlow()

    private val _canLoadMore = MutableStateFlow(false)
    val canLoadMore: StateFlow<Boolean> = _canLoadMore.asStateFlow()

    private val accumulated = mutableListOf<Song>()
    private var albumOffset = 0
    private var loading = false

    init {
        load()
    }

    fun load() {
        if (loading) return
        accumulated.clear()
        albumOffset = 0
        _canLoadMore.value = false
        viewModelScope.launch {
            _state.value = LoadState.Loading
            fetchPage(initial = true)
        }
    }

    fun loadMore() {
        if (loading || !_canLoadMore.value) return
        viewModelScope.launch { fetchPage(initial = false) }
    }

    fun setSort(sort: SongSort) {
        _sort.value = sort
        if (accumulated.isEmpty()) return
        _state.value = LoadState.Data(accumulated.orderedBy(sort))
    }

    fun playFrom(startIndex: Int) {
        val songs = currentSongs() ?: return
        playbackController.playSongs(songs, startIndex)
    }

    fun playAll() = playFrom(0)

    fun playShuffled() {
        val songs = currentSongs() ?: return
        playbackController.playSongs(songs, shuffle = true)
    }

    private fun currentSongs(): List<Song>? =
        (_state.value as? LoadState.Data)?.value?.takeIf { it.isNotEmpty() }

    private suspend fun fetchPage(initial: Boolean) {
        loading = true
        runCatching { allSongsRepository.loadPage(albumOffset, PAGE_ALBUMS) }.fold(
            onSuccess = { page ->
                albumOffset += page.albumCount
                val seen = accumulated.mapTo(HashSet()) { it.id }
                accumulated += page.songs.filter { seen.add(it.id) }
                _canLoadMore.value = page.albumCount == PAGE_ALBUMS
                _state.value = LoadState.Data(accumulated.orderedBy(_sort.value))
            },
            onFailure = { error ->
                if (initial) _state.value = LoadState.Error(error.toUserMessage())
            },
        )
        loading = false
    }

    private companion object {
        const val PAGE_ALBUMS = 12
    }
}
