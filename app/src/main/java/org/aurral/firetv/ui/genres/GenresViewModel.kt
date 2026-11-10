package org.aurral.firetv.ui.genres

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.aurral.firetv.data.model.Genre
import org.aurral.firetv.data.repository.LibraryRepository
import org.aurral.firetv.ui.common.LoadState
import org.aurral.firetv.ui.common.toUserMessage
import javax.inject.Inject

@HiltViewModel
class GenresViewModel @Inject constructor(
    private val libraryRepository: LibraryRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<LoadState<List<Genre>>>(LoadState.Loading)
    val state: StateFlow<LoadState<List<Genre>>> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = LoadState.Loading
            _state.value = runCatching { libraryRepository.genres() }.fold(
                onSuccess = { LoadState.Data(it.sortedBy { genre -> genre.name.lowercase() }) },
                onFailure = { LoadState.Error(it.toUserMessage()) },
            )
        }
    }
}
