package org.aurral.firetv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
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
import org.aurral.firetv.ui.common.formatDurationMs
import org.aurral.firetv.ui.player.PlayerViewModel
import org.aurral.firetv.ui.theme.Surface

/** Persistent now-playing strip shown under every screen. */
@Composable
fun MiniPlayerBar(
    onOpenNowPlaying: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    if (!state.hasCurrent) return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Surface)
            .clickable(onClick = onOpenNowPlaying)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        CoverArt(
            coverArtId = state.coverArtId,
            modifier = Modifier.size(56.dp),
            requestSizePx = 168,
            placeholderIconSize = 20.dp,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = state.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = state.artist,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (state.isBuffering) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
        }
        IconButton(onClick = { viewModel.previous() }) {
            Icon(Icons.Filled.SkipPrevious, contentDescription = "Previous")
        }
        IconButton(onClick = { viewModel.togglePlayPause() }) {
            Icon(
                imageVector = if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (state.isPlaying) "Pause" else "Play",
            )
        }
        IconButton(onClick = { viewModel.next() }) {
            Icon(Icons.Filled.SkipNext, contentDescription = "Next")
        }
        Text(
            text = formatDurationMs(state.durationMs),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
