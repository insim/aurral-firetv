package org.aurral.firetv.ui.playlists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import org.aurral.firetv.data.model.Playlist
import org.aurral.firetv.ui.common.LoadState
import org.aurral.firetv.ui.common.LoadStateContent
import org.aurral.firetv.ui.common.formatDuration
import org.aurral.firetv.ui.components.ConfirmDialog
import org.aurral.firetv.ui.components.CoverArt
import org.aurral.firetv.ui.components.FocusHighlight
import org.aurral.firetv.ui.components.SongRow
import org.aurral.firetv.ui.components.TextInputDialog
import org.aurral.firetv.ui.player.PlayerViewModel

@Composable
fun PlaylistDetailScreen(
    playlistId: String,
    onBack: () -> Unit,
    viewModel: PlaylistDetailViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val playback by playerViewModel.state.collectAsStateWithLifecycle()
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LoadStateContent(state = state, onRetry = viewModel::load) { detail ->
        LaunchedEffect(detail.deleted) {
            if (detail.deleted) onBack()
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp),
        ) {
            item {
                PlaylistHeader(
                    playlist = detail.playlist,
                    onPlayAll = { viewModel.playFrom(0) },
                    onRename = { showRenameDialog = true },
                    onDelete = { showDeleteDialog = true },
                )
            }
            detail.errorMessage?.let { message ->
                item {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp),
                    )
                }
            }
            itemsIndexed(detail.playlist.songs, key = { _, song -> song.id }) { index, song ->
                SongRow(
                    position = index + 1,
                    title = song.title,
                    subtitle = song.artist,
                    durationSeconds = song.durationSeconds,
                    isPlaying = song.id == playback.currentSongId,
                    isStarred = song.starred,
                    onClick = { viewModel.playFrom(index) },
                    onRemove = { viewModel.removeAt(index) },
                )
            }
        }
    }

    if (showRenameDialog) {
        TextInputDialog(
            title = "Rename playlist",
            label = "Name",
            initialValue = (state as? LoadState.Data)?.value?.playlist?.name.orEmpty(),
            onConfirm = { name ->
                viewModel.rename(name)
                showRenameDialog = false
            },
            onDismiss = { showRenameDialog = false },
        )
    }

    if (showDeleteDialog) {
        ConfirmDialog(
            title = "Delete playlist",
            message = "This removes the playlist from Aurral. The tracks stay in your library.",
            confirmLabel = "Delete",
            onConfirm = {
                viewModel.delete()
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false },
        )
    }
}

@Composable
private fun PlaylistHeader(
    playlist: Playlist,
    onPlayAll: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier.padding(horizontal = 32.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CoverArt(
            coverArtId = playlist.coverArtId,
            modifier = Modifier.size(160.dp),
            requestSizePx = 500,
            placeholderIconSize = 44.dp,
        )
        Column {
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "${playlist.songCount} songs · ${formatDuration(playlist.durationSeconds)}" +
                    (playlist.owner?.let { " · $it" } ?: ""),
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
                    OutlinedButton(onClick = onRename) {
                        Icon(Icons.Filled.Edit, contentDescription = null)
                        Text(text = "Rename", modifier = Modifier.padding(start = 8.dp))
                    }
                }
                FocusHighlight {
                    OutlinedButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = null)
                        Text(text = "Delete", modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        }
    }
}
