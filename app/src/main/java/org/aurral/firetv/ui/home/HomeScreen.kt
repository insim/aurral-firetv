package org.aurral.firetv.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.aurral.firetv.data.model.Album
import org.aurral.firetv.ui.common.LoadStateContent
import org.aurral.firetv.ui.components.CoverArt
import org.aurral.firetv.ui.components.FocusHighlight
import org.aurral.firetv.ui.components.FocusableCard
import org.aurral.firetv.ui.components.MediaCard
import org.aurral.firetv.ui.components.MediaCarousel
import org.aurral.firetv.ui.components.SectionHeader
import org.aurral.firetv.ui.theme.Dimens

@Composable
fun HomeScreen(
    onOpenAlbum: (String) -> Unit,
    onOpenPlaylist: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LoadStateContent(state = state, onRetry = viewModel::load) { content ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = Dimens.ScreenPadding),
        ) {
            item { SectionHeader(title = "Home", subtitle = "Your Aurral library") }
            item {
                HomeMixActions(
                    onShuffleLibrary = viewModel::shuffleLibrary,
                    onPlayMix = viewModel::playBlendedMix,
                )
            }
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
            if (content.playlists.isNotEmpty()) {
                item {
                    MediaCarousel(
                        title = "Playlists & Flows",
                        items = content.playlists,
                        key = { it.id },
                    ) { playlist ->
                        MediaCard(
                            title = playlist.name,
                            subtitle = playlist.owner,
                            coverArtId = playlist.coverArtId,
                            onClick = { onOpenPlaylist(playlist.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeMixActions(
    onShuffleLibrary: () -> Unit,
    onPlayMix: () -> Unit,
) {
    Row(
        modifier = Modifier.padding(horizontal = Dimens.ScreenPadding, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        FocusHighlight {
            OutlinedButton(onClick = onShuffleLibrary) {
                Icon(
                    imageVector = Icons.Filled.Shuffle,
                    contentDescription = null,
                    modifier = Modifier.size(Dimens.Icon),
                )
                Text(text = "Shuffle library", modifier = Modifier.padding(start = 6.dp))
            }
        }
        FocusHighlight {
            OutlinedButton(onClick = onPlayMix) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(Dimens.Icon),
                )
                Text(text = "Mix", modifier = Modifier.padding(start = 6.dp))
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
        modifier = Modifier
            .padding(horizontal = Dimens.ScreenPadding, vertical = 8.dp)
            .fillMaxWidth(0.6f),
        cornerRadius = 12.dp,
        focusedScale = 1.02f,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CoverArt(
                coverArtId = album.coverArtId,
                modifier = Modifier.size(140.dp),
                requestSizePx = 500,
                placeholderIconSize = Dimens.IconLarge,
            )
            Column {
                Text(
                    text = album.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = album.artist,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp),
                )
                Text(
                    text = "${album.songCount} tracks" + (album.year?.let { " · $it" } ?: ""),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}
