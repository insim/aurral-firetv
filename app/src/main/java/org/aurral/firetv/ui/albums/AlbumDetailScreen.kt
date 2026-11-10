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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material3.Button
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
import org.aurral.firetv.ui.components.PlaylistPickerDialog
import org.aurral.firetv.ui.components.SongRow
import org.aurral.firetv.ui.components.TextInputDialog
import org.aurral.firetv.ui.player.PlayerViewModel

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
            contentPadding = PaddingValues(bottom = 32.dp),
        ) {
            item {
                AlbumHeader(
                    album = detail.album,
                    onPlayAll = { viewModel.playFrom(0) },
                    onAddToPlaylist = {
                        viewModel.loadPlaylists()
                        showPlaylistPicker = true
                    },
                )
            }
            itemsIndexed(detail.album.songs, key = { _, song -> song.id }) { index, song ->
                SongRow(
                    position = index + 1,
                    title = song.title,
                    subtitle = song.artist,
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
    onAddToPlaylist: () -> Unit,
) {
    Row(
        modifier = Modifier.padding(horizontal = 32.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CoverArt(
            coverArtId = album.coverArtId,
            modifier = Modifier.size(180.dp),
            requestSizePx = 600,
            placeholderIconSize = 48.dp,
        )
        Column {
            Text(
                text = album.name,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = album.artist,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
            val details = buildList {
                add("${album.songCount} tracks")
                album.year?.let { add(it.toString()) }
                album.genre?.let { add(it) }
            }.joinToString(" · ")
            Text(
                text = details,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 16.dp),
            ) {
                FocusHighlight {
                    Button(onClick = onPlayAll) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null)
                        Text(text = "Play", modifier = Modifier.padding(start = 8.dp))
                    }
                }
                FocusHighlight {
                    OutlinedButton(onClick = onAddToPlaylist) {
                        Icon(Icons.Filled.PlaylistAdd, contentDescription = null)
                        Text(text = "Add to playlist", modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        }
    }
}
