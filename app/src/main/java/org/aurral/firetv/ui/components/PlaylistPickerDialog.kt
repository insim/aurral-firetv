package org.aurral.firetv.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.aurral.firetv.data.model.Playlist

/** Lets the user pick a playlist, or start creating a new one. */
@Composable
fun PlaylistPickerDialog(
    playlists: List<Playlist>,
    onSelect: (Playlist) -> Unit,
    onCreateNew: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add to playlist") },
        text = {
            if (playlists.isEmpty()) {
                Text("You have no playlists yet.")
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 320.dp)) {
                    items(playlists, key = { it.id }) { playlist ->
                        Text(
                            text = playlist.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(playlist) }
                                .padding(vertical = 12.dp),
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onCreateNew) { Text("New playlist") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
