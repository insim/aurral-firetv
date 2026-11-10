package org.aurral.firetv.ui.artists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.aurral.firetv.data.model.Artist
import org.aurral.firetv.data.model.ArtistSort
import org.aurral.firetv.data.model.orderedBy
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

    private val _sort = MutableStateFlow(ArtistSort.NAME)
    val sort: StateFlow<ArtistSort> = _sort.asStateFlow()

    private var artists: List<Artist> = emptyList()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = LoadState.Loading
            runCatching { libraryRepository.artists() }.fold(
                onSuccess = { loaded ->
                    artists = loaded
                    _state.value = LoadState.Data(loaded.orderedBy(_sort.value))
                },
                onFailure = { _state.value = LoadState.Error(it.toUserMessage()) },
            )
        }
    }

    fun setSort(sort: ArtistSort) {
        _sort.value = sort
        if (artists.isNotEmpty()) {
            _state.value = LoadState.Data(artists.orderedBy(sort))
        }
    }
}
