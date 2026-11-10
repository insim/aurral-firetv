package org.aurral.firetv.ui.artists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import org.aurral.firetv.ui.common.LoadStateContent
import org.aurral.firetv.ui.components.MediaCard
import org.aurral.firetv.ui.components.MediaCarousel
import org.aurral.firetv.ui.components.SectionHeader
import org.aurral.firetv.ui.components.SongRow
import org.aurral.firetv.ui.player.PlayerViewModel

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
            contentPadding = PaddingValues(bottom = 32.dp),
        ) {
            item {
                ArtistHeader(name = detail.artist.name, albumCount = detail.artist.albumCount)
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
                item { SectionHeader(title = "Top Songs") }
                itemsIndexed(detail.topSongs, key = { _, song -> song.id }) { index, song ->
                    SongRow(
                        position = index + 1,
                        title = song.title,
                        subtitle = song.album,
                        durationSeconds = song.durationSeconds,
                        isPlaying = song.id == playback.currentSongId,
                        isStarred = song.starred,
                        onClick = { viewModel.playSongs(detail.topSongs, index) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ArtistHeader(name: String, albumCount: Int) {
    Row(
        modifier = Modifier.padding(horizontal = 32.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "$albumCount albums",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
