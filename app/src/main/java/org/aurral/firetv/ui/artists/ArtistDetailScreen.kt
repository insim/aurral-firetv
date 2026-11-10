package org.aurral.firetv.ui.artists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.aurral.firetv.ui.common.LoadStateContent
import org.aurral.firetv.ui.components.FocusHighlight
import org.aurral.firetv.ui.components.MediaCard
import org.aurral.firetv.ui.components.MediaCarousel
import org.aurral.firetv.ui.components.PlayActionButtons
import org.aurral.firetv.ui.components.SectionHeader
import org.aurral.firetv.ui.components.SongRow
import org.aurral.firetv.ui.player.PlayerViewModel
import org.aurral.firetv.ui.theme.Dimens

@Composable
fun ArtistDetailScreen(
    artistId: String,
    onOpenAlbum: (String) -> Unit,
    viewModel: ArtistDetailViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val playback by playerViewModel.state.collectAsStateWithLifecycle()

    LoadStateContent(state = state, onRetry = viewModel::load) { detail ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = Dimens.ScreenPadding),
        ) {
            item {
                ArtistHeader(
                    name = detail.artist.name,
                    albumCount = detail.artist.albumCount,
                    bio = detail.bio,
                    hasSongs = detail.topSongs.isNotEmpty(),
                    onPlayAll = { viewModel.playFrom(0) },
                    onShuffle = viewModel::playShuffled,
                    onRadio = viewModel::playRadio,
                )
            }
            if (detail.artist.albums.isNotEmpty()) {
                item {
                    MediaCarousel(
                        title = "Albums",
                        items = detail.artist.albums,
                        key = { it.id },
                    ) { album ->
                        MediaCard(
                            title = album.name,
                            subtitle = album.year?.toString(),
                            coverArtId = album.coverArtId,
                            onClick = { onOpenAlbum(album.id) },
                        )
                    }
                }
            }
            if (detail.topSongs.isNotEmpty()) {
                item { SectionHeader(title = "Popular") }
                itemsIndexed(detail.topSongs, key = { _, song -> song.id }) { index, song ->
                    SongRow(
                        title = song.title,
                        subtitle = song.album,
                        coverArtId = song.coverArtId,
                        durationSeconds = song.durationSeconds,
                        isPlaying = song.id == playback.currentSongId,
                        isStarred = song.starred,
                        onClick = { viewModel.playFrom(index) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ArtistHeader(
    name: String,
    albumCount: Int,
    bio: String,
    hasSongs: Boolean,
    onPlayAll: () -> Unit,
    onShuffle: () -> Unit,
    onRadio: () -> Unit,
) {
    Row(
        modifier = Modifier.padding(horizontal = Dimens.ScreenPadding, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "$albumCount albums",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (bio.isNotBlank()) {
                Text(
                    text = bio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(0.75f).padding(top = 6.dp),
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(top = 10.dp),
            ) {
                PlayActionButtons(
                    onPlayAll = onPlayAll,
                    onShuffle = onShuffle,
                    enabled = hasSongs,
                )
                FocusHighlight {
                    OutlinedButton(onClick = onRadio) {
                        Icon(
                            imageVector = Icons.Filled.Radio,
                            contentDescription = null,
                            modifier = Modifier.size(Dimens.Icon),
                        )
                        Text(text = "Radio", modifier = Modifier.padding(start = 6.dp))
                    }
                }
            }
        }
    }
}
