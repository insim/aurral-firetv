package org.aurral.firetv.ui.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import org.aurral.firetv.ui.components.MediaCard
import org.aurral.firetv.ui.components.MediaCarousel
import org.aurral.firetv.ui.components.SectionHeader
import org.aurral.firetv.ui.components.SongRow

@Composable
fun SearchScreen(
    onOpenAlbum: (String) -> Unit,
    onOpenArtist: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val results = uiState.results

    Column(modifier = Modifier.fillMaxSize().padding(top = 24.dp)) {
        SectionHeader(title = "Search")

        OutlinedTextField(
            value = uiState.query,
            onValueChange = viewModel::onQueryChange,
            label = { Text("Artists, albums, tracks") },
            singleLine = true,
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            modifier = Modifier.fillMaxWidth(0.6f).padding(horizontal = 32.dp),
        )

        uiState.errorMessage?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp),
            )
        }

        if (!uiState.isSearching && results.isEmpty && uiState.query.isNotBlank()) {
            Text(
                text = "No results for \"${uiState.query}\"",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp),
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp),
        ) {
            if (results.artists.isNotEmpty()) {
                item {
                    MediaCarousel(
                        title = "Artists",
                        items = results.artists,
                        key = { it.id },
                    ) { artist ->
                        MediaCard(
                            title = artist.name,
                            subtitle = "${artist.albumCount} albums",
                            coverArtId = artist.coverArtId,
                            onClick = { onOpenArtist(artist.id) },
                        )
                    }
                }
            }
            if (results.albums.isNotEmpty()) {
                item {
                    MediaCarousel(
                        title = "Albums",
                        items = results.albums,
                        key = { it.id },
                    ) { album ->
                        MediaCard(
                            title = album.name,
                            subtitle = album.artist,
                            coverArtId = album.coverArtId,
                            onClick = { onOpenAlbum(album.id) },
                        )
                    }
                }
            }
            if (results.songs.isNotEmpty()) {
                item { SectionHeader(title = "Tracks") }
                itemsIndexed(results.songs, key = { _, song -> song.id }) { index, song ->
                    SongRow(
                        position = index + 1,
                        title = song.title,
                        subtitle = song.artist,
                        durationSeconds = song.durationSeconds,
                        isPlaying = false,
                        isStarred = song.starred,
                        onClick = { song.albumId?.let(onOpenAlbum) },
                    )
                }
            }
        }
    }
}
