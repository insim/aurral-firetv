package org.aurral.firetv.ui.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.aurral.firetv.data.model.Album
import org.aurral.firetv.ui.common.LoadStateContent
import org.aurral.firetv.ui.components.CoverArt
import org.aurral.firetv.ui.components.FocusableCard
import org.aurral.firetv.ui.components.MediaCard
import org.aurral.firetv.ui.components.MediaCarousel
import org.aurral.firetv.ui.components.SectionHeader
import org.aurral.firetv.ui.theme.BrandWash
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
                HomeMixCards(
                    onPlayMix = viewModel::playBlendedMix,
                    onShuffleLibrary = viewModel::shuffleLibrary,
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
private fun HomeMixCards(
    onPlayMix: () -> Unit,
    onShuffleLibrary: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.ScreenPadding, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        MixCard(
            title = "Mix",
            subtitle = "Recent, favourites and something new",
            onClick = onPlayMix,
            modifier = Modifier.weight(1f),
        )
        MixCard(
            title = "Shuffle library",
            subtitle = "Everything, in a random order",
            onClick = onShuffleLibrary,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun MixCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (focused) 1.02f else 1f, label = "mixCardScale")
    val shape = RoundedCornerShape(14.dp)

    Box(
        modifier = modifier
            .scale(scale)
            .clip(shape)
            .background(BrandWash)
            .border(
                width = if (focused) 2.dp else 0.dp,
                color = if (focused) Color.White else Color.Transparent,
                shape = shape,
            )
            .onFocusChanged { focused = it.isFocused }
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp),
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MixSubtitle,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

private val MixSubtitle = Color(0xFFD8D3F7)

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
