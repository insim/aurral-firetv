package org.aurral.firetv.ui.playlists

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.aurral.firetv.data.model.Playlist
import org.aurral.firetv.data.model.PlaylistSort
import org.aurral.firetv.ui.common.LoadStateListContent
import org.aurral.firetv.ui.common.formatDuration
import org.aurral.firetv.ui.components.ActionRow
import org.aurral.firetv.ui.components.FocusHighlight
import org.aurral.firetv.ui.components.SectionHeader
import org.aurral.firetv.ui.components.SortSelector
import org.aurral.firetv.ui.components.TextInputDialog
import org.aurral.firetv.ui.theme.Dimens

@Composable
fun PlaylistsScreen(
    onOpenPlaylist: (String) -> Unit,
    viewModel: PlaylistsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sort by viewModel.sort.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
        SectionHeader(title = "Playlists")
        SortSelector(
            options = PlaylistSort.entries,
            selected = sort,
            labelOf = { it.label },
            onSelect = viewModel::setSort,
            modifier = Modifier.padding(horizontal = Dimens.ScreenPadding, vertical = 4.dp),
        )

        FocusHighlight(modifier = Modifier.padding(start = Dimens.ScreenPadding, top = 4.dp, bottom = 4.dp)) {
            Button(onClick = { showCreateDialog = true }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.size(Dimens.Icon),
                )
                Text(text = "New playlist", modifier = Modifier.padding(start = 6.dp))
            }
        }

        LoadStateListContent(
            state = state,
            onRetry = viewModel::load,
            emptyMessage = "No playlists or flows yet",
        ) { playlists ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = Dimens.ScreenPadding, vertical = 4.dp),
            ) {
                items(playlists, key = { it.id }) { playlist ->
                    ActionRow(
                        title = playlist.name,
                        subtitle = playlistSubtitle(playlist),
                        coverArtId = playlist.coverArtId,
                        onClick = { onOpenPlaylist(playlist.id) },
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        TextInputDialog(
            title = "New playlist",
            label = "Name",
            confirmLabel = "Create",
            onConfirm = { name ->
                viewModel.createPlaylist(name)
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false },
        )
    }
}

private fun playlistSubtitle(playlist: Playlist): String {
    val owner = playlist.owner?.let { " · $it" }.orEmpty()
    return "${playlist.songCount} songs · ${formatDuration(playlist.durationSeconds)}$owner"
}
