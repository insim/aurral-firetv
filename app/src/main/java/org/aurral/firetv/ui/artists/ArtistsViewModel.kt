package org.aurral.firetv.ui.artists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.aurral.firetv.data.model.Artist
import org.aurral.firetv.data.repository.LibraryRepository
import org.aurral.firetv.ui.common.LoadState
import org.aurral.firetv.ui.common.toUserMessage
import javax.inject.Inject

@HiltViewModel
class ArtistsViewModel @Inject constructor(
    private val libraryRepository: LibraryRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<LoadState<List<Artist>>>(LoadState.Loading)
    val state: StateFlow<LoadState<List<Artist>>> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = LoadState.Loading
            _state.value = runCatching { libraryRepository.artists() }.fold(
                onSuccess = { LoadState.Data(it) },
                onFailure = { LoadState.Error(it.toUserMessage()) },
            )
        }
    }
}
