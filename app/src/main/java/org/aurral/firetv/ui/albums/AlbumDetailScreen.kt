package org.aurral.firetv.ui.albums

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.aurral.firetv.data.model.Album
import org.aurral.firetv.ui.common.LoadStateContent
import org.aurral.firetv.ui.components.CoverArt
import org.aurral.firetv.ui.components.FocusHighlight
import org.aurral.firetv.ui.components.PlayActionButtons
import org.aurral.firetv.ui.components.PlaylistPickerDialog
import org.aurral.firetv.ui.components.SongRow
import org.aurral.firetv.ui.components.TextInputDialog
import org.aurral.firetv.ui.player.PlayerViewModel
import org.aurral.firetv.ui.theme.Dimens

@Composable
fun AlbumDetailScreen(
    albumId: String,
    onOpenArtist: (String) -> Unit,
    viewModel: AlbumDetailViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val playback by playerViewModel.state.collectAsStateWithLifecycle()
    var showPlaylistPicker by remember { mutableStateOf(false) }
    var showCreatePlaylist by remember { mutableStateOf(false) }

    LoadStateContent(state = state, onRetry = viewModel::load) { detail ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = Dimens.ScreenPadding),
        ) {
            item {
                AlbumHeader(
                    album = detail.album,
                    onPlayAll = { viewModel.playFrom(0) },
                    onShuffle = viewModel::playShuffled,
                    onAddToPlaylist = {
                        viewModel.loadPlaylists()
                        showPlaylistPicker = true
                    },
                )
            }
            itemsIndexed(detail.album.songs, key = { _, song -> song.id }) { index, song ->
                SongRow(
                    title = song.title,
                    subtitle = song.artist,
                    coverArtId = song.coverArtId,
                    durationSeconds = song.durationSeconds,
                    isPlaying = song.id == playback.currentSongId,
                    isStarred = song.starred,
                    onClick = { viewModel.playFrom(index) },
                    onToggleStar = { viewModel.toggleSongStar(song) },
                )
            }
        }
    }

    if (showPlaylistPicker) {
        PlaylistPickerDialog(
            playlists = playlists,
            onSelect = { playlist ->
                viewModel.addAlbumToPlaylist(playlist.id)
                showPlaylistPicker = false
            },
            onCreateNew = {
                showPlaylistPicker = false
                showCreatePlaylist = true
            },
            onDismiss = { showPlaylistPicker = false },
        )
    }

    if (showCreatePlaylist) {
        TextInputDialog(
            title = "New playlist",
            label = "Name",
            confirmLabel = "Create",
            onConfirm = { name ->
                viewModel.createPlaylistWithAlbum(name)
                showCreatePlaylist = false
            },
            onDismiss = { showCreatePlaylist = false },
        )
    }
}

@Composable
private fun AlbumHeader(
    album: Album,
    onPlayAll: () -> Unit,
    onShuffle: () -> Unit,
    onAddToPlaylist: () -> Unit,
) {
    Row(
        modifier = Modifier.padding(horizontal = Dimens.ScreenPadding, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CoverArt(
            coverArtId = album.coverArtId,
            modifier = Modifier.size(120.dp),
            requestSizePx = 480,
            placeholderIconSize = Dimens.IconLarge,
        )
        Column {
            Text(
                text = album.name,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = album.artist,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            val details = buildList {
                add("${album.songCount} tracks")
                album.year?.let { add(it.toString()) }
                album.genre?.let { add(it) }
            }.joinToString(" · ")
            Text(
                text = details,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp),
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(top = 10.dp),
            ) {
                PlayActionButtons(onPlayAll = onPlayAll, onShuffle = onShuffle)
                FocusHighlight {
                    OutlinedButton(onClick = onAddToPlaylist) {
                        Icon(
                            imageVector = Icons.Filled.PlaylistAdd,
                            contentDescription = null,
                            modifier = Modifier.size(Dimens.Icon),
                        )
                        Text(text = "Add", modifier = Modifier.padding(start = 6.dp))
                    }
                }
            }
        }
    }
}
