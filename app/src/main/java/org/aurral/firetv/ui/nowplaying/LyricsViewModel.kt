package org.aurral.firetv.ui.nowplaying

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.aurral.firetv.data.model.Lyrics
import org.aurral.firetv.data.repository.LyricsRepository
import org.aurral.firetv.ui.common.LoadState
import org.aurral.firetv.ui.common.toUserMessage
import org.aurral.firetv.ui.navigation.Routes
import javax.inject.Inject

@HiltViewModel
class LyricsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val lyricsRepository: LyricsRepository,
) : ViewModel() {

    val songId: String = savedStateHandle.get<String>(Routes.SONG_ID).orEmpty()

    private val _state = MutableStateFlow<LoadState<Lyrics>>(LoadState.Loading)
    val state: StateFlow<LoadState<Lyrics>> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = LoadState.Loading
            _state.value = runCatching { lyricsRepository.lyrics(songId) }.fold(
                onSuccess = { LoadState.Data(it) },
                onFailure = { LoadState.Error(it.toUserMessage()) },
            )
        }
    }
}
