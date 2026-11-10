package org.aurral.firetv.ui.search

import org.aurral.firetv.data.model.SearchResults

data class SearchUiState(
    val query: String = "",
    val results: SearchResults = SearchResults(emptyList(), emptyList(), emptyList()),
    val isSearching: Boolean = false,
    val errorMessage: String? = null,
)
