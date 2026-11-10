package org.aurral.firetv.data.repository

import org.aurral.firetv.data.api.SubsonicApi
import org.aurral.firetv.data.api.orThrow
import org.aurral.firetv.data.mapper.toSearchResults
import org.aurral.firetv.data.model.SearchResults
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    private val api: SubsonicApi,
) {

    suspend fun search(query: String): SearchResults {
        if (query.isBlank()) return SearchResults(emptyList(), emptyList(), emptyList())
        return api.search3(query).orThrow().searchResult3?.toSearchResults()
            ?: SearchResults(emptyList(), emptyList(), emptyList())
    }
}
