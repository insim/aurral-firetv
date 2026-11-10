package org.aurral.firetv.ui.nowplaying

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import org.aurral.firetv.playback.QueueItem
import org.aurral.firetv.ui.common.formatDurationMs
import org.aurral.firetv.ui.components.EmptyState
import org.aurral.firetv.ui.components.FocusableCard
import org.aurral.firetv.ui.components.SectionHeader
import org.aurral.firetv.ui.player.PlayerViewModel
import org.aurral.firetv.ui.theme.Accent

@Composable
fun QueueScreen(viewModel: PlayerViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.queue.isEmpty()) {
        EmptyState(message = "The queue is empty.")
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp),
    ) {
        item { SectionHeader(title = "Queue", subtitle = "${state.queue.size} tracks") }
        itemsIndexed(state.queue, key = { _, item -> item.id }) { index, item ->
            QueueRow(
                item = item,
                isCurrent = index == state.currentIndex,
                canMoveUp = index > 0,
                canMoveDown = index < state.queue.lastIndex,
                onClick = { viewModel.seekToQueueIndex(index) },
                onRemove = { viewModel.removeFromQueue(index) },
                onMoveUp = { viewModel.moveInQueue(index, index - 1) },
                onMoveDown = { viewModel.moveInQueue(index, index + 1) },
            )
        }
    }
}

@Composable
private fun QueueRow(
    item: QueueItem,
    isCurrent: Boolean,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
) {
    FocusableCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 4.dp),
        focusedScale = 1.01f,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (isCurrent) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Now playing",
                    tint = Accent,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = item.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = formatDurationMs(item.durationMs),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            IconButton(onClick = onMoveUp, enabled = canMoveUp) {
                Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Move up")
            }
            IconButton(onClick = onMoveDown, enabled = canMoveDown) {
                Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Move down")
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Filled.Delete, contentDescription = "Remove from queue")
            }
        }
    }
}
