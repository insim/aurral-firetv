package org.aurral.firetv.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.aurral.firetv.data.model.SearchResults
import org.aurral.firetv.data.repository.SearchRepository
import org.aurral.firetv.ui.common.toUserMessage
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(value: String) {
        _uiState.update { it.copy(query = value, errorMessage = null) }
        searchJob?.cancel()

        if (value.isBlank()) {
            _uiState.update {
                it.copy(results = SearchResults(emptyList(), emptyList(), emptyList()), isSearching = false)
            }
            return
        }

        searchJob = viewModelScope.launch {
            delay(DEBOUNCE_MS)
            _uiState.update { it.copy(isSearching = true) }
            runCatching { searchRepository.search(value) }.fold(
                onSuccess = { results ->
                    _uiState.update { it.copy(results = results, isSearching = false) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(errorMessage = error.toUserMessage(), isSearching = false) }
                },
            )
        }
    }

    fun retry() = onQueryChange(_uiState.value.query)

    private companion object {
        const val DEBOUNCE_MS = 350L
    }
}
