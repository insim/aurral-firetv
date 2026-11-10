package org.aurral.firetv.ui.albums

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.aurral.firetv.data.model.Album
import org.aurral.firetv.data.model.AlbumListType
import org.aurral.firetv.data.repository.LibraryRepository
import org.aurral.firetv.ui.common.LoadState
import org.aurral.firetv.ui.common.toUserMessage
import javax.inject.Inject

/**
 * Pages through the whole library one page at a time, so a very large collection
 * costs one request and one screenful of models at a time.
 */
@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val libraryRepository: LibraryRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<LoadState<List<Album>>>(LoadState.Loading)
    val state: StateFlow<LoadState<List<Album>>> = _state.asStateFlow()

    private val _canLoadMore = MutableStateFlow(false)
    val canLoadMore: StateFlow<Boolean> = _canLoadMore.asStateFlow()

    private var offset = 0
    private var loadingMore = false

    init {
        load()
    }

    fun load() {
        offset = 0
        loadingMore = false
        _canLoadMore.value = false
        viewModelScope.launch {
            _state.value = LoadState.Loading
            runCatching { libraryRepository.albumList(PAGE_TYPE, PAGE_SIZE, offset = 0) }.fold(
                onSuccess = { page ->
                    offset = page.size
                    _canLoadMore.value = page.size == PAGE_SIZE
                    _state.value = LoadState.Data(page)
                },
                onFailure = { _state.value = LoadState.Error(it.toUserMessage()) },
            )
        }
    }

    fun loadMore() {
        if (loadingMore || !_canLoadMore.value) return
        loadingMore = true
        viewModelScope.launch {
            runCatching { libraryRepository.albumList(PAGE_TYPE, PAGE_SIZE, offset) }.fold(
                onSuccess = { page ->
                    if (page.isEmpty()) {
                        _canLoadMore.value = false
                    } else {
                        offset += page.size
                        _canLoadMore.value = page.size == PAGE_SIZE
                        val loaded = (_state.value as? LoadState.Data)?.value.orEmpty()
                        _state.value = LoadState.Data(loaded + page)
                    }
                },
                onFailure = { },
            )
            loadingMore = false
        }
    }

    private companion object {
        val PAGE_TYPE = AlbumListType.ALPHABETICAL_BY_NAME
        const val PAGE_SIZE = 100
    }
}
