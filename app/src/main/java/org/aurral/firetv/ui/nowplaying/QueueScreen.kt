package org.aurral.firetv.ui.nowplaying

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
import org.aurral.firetv.playback.QueueItem
import org.aurral.firetv.ui.common.formatDurationMs
import org.aurral.firetv.ui.components.CoverArt
import org.aurral.firetv.ui.components.EmptyState
import org.aurral.firetv.ui.components.FocusableCard
import org.aurral.firetv.ui.components.SectionHeader
import org.aurral.firetv.ui.player.PlayerViewModel
import org.aurral.firetv.ui.theme.Accent
import org.aurral.firetv.ui.theme.Dimens

@Composable
fun QueueScreen(viewModel: PlayerViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.queue.isEmpty()) {
        EmptyState(message = "The queue is empty.")
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = Dimens.ScreenPadding),
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.ScreenPadding, vertical = 2.dp),
        focusedScale = 1.01f,
        cornerRadius = 6.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            CoverArt(
                coverArtId = item.coverArtId,
                modifier = Modifier.size(Dimens.RowCoverArt),
                requestSizePx = 120,
                placeholderIconSize = Dimens.IconSmall,
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isCurrent) Accent else MaterialTheme.colorScheme.onSurface,
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
            if (isCurrent) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Now playing",
                    tint = Accent,
                    modifier = Modifier.size(Dimens.Icon),
                )
            }
            Text(
                text = formatDurationMs(item.durationMs),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            IconButton(onClick = onMoveUp, enabled = canMoveUp, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Move up",
                    modifier = Modifier.size(Dimens.Icon),
                )
            }
            IconButton(onClick = onMoveDown, enabled = canMoveDown, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Move down",
                    modifier = Modifier.size(Dimens.Icon),
                )
            }
            IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Remove from queue",
                    modifier = Modifier.size(Dimens.Icon),
                )
            }
        }
    }
}
