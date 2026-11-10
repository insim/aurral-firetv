package org.aurral.firetv.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.aurral.firetv.data.model.Album
import org.aurral.firetv.ui.common.LoadStateContent
import org.aurral.firetv.ui.components.CoverArt
import org.aurral.firetv.ui.components.FocusableCard
import org.aurral.firetv.ui.components.MediaCard
import org.aurral.firetv.ui.components.MediaCarousel
import org.aurral.firetv.ui.components.SectionHeader

@Composable
fun HomeScreen(
    onOpenAlbum: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LoadStateContent(state = state, onRetry = viewModel::load) { content ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp),
        ) {
            item { SectionHeader(title = "Home", subtitle = "Your Aurral library") }
            content.heroAlbum?.let { album ->
                item { HomeHero(album = album, onOpenAlbum = onOpenAlbum) }
            }
            items(content.rails) { rail ->
                MediaCarousel(
                    title = rail.title,
                    items = rail.albums,
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
    }
}

@Composable
private fun HomeHero(
    album: Album,
    onOpenAlbum: (String) -> Unit,
) {
    FocusableCard(
        onClick = { onOpenAlbum(album.id) },
        modifier = Modifier.padding(horizontal = 32.dp, vertical = 12.dp).fillMaxWidth(0.75f),
        cornerRadius = 16.dp,
        focusedScale = 1.02f,
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            CoverArt(
                coverArtId = album.coverArtId,
                modifier = Modifier.size(200.dp),
                requestSizePx = 600,
                placeholderIconSize = 56.dp,
            )
            Column {
                Text(
                    text = album.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = album.artist,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Text(
                    text = "${album.songCount} tracks" + (album.year?.let { " · $it" } ?: ""),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}
