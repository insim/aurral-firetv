package org.aurral.firetv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.aurral.firetv.ui.common.formatDurationMs
import org.aurral.firetv.ui.player.PlayerViewModel
import org.aurral.firetv.ui.theme.Dimens
import org.aurral.firetv.ui.theme.Surface

/**
 * Persistent now-playing strip shown under every screen.
 *
 * The artwork/title area and each transport button are separate focus targets, so
 * the D-pad can reach the controls rather than being captured by the whole bar.
 */
@Composable
fun MiniPlayerBar(
    onOpenNowPlaying: () -> Unit,
    onOpenQueue: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    if (!state.hasCurrent) return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.MiniPlayerHeight)
            .background(Surface)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FocusableCard(
            onClick = onOpenNowPlaying,
            modifier = Modifier.weight(1f),
            cornerRadius = Dimens.CornerRadius,
            focusedScale = 1f,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                CoverArt(
                    coverArtId = state.coverArtId,
                    modifier = Modifier.size(40.dp),
                    requestSizePx = 120,
                    placeholderIconSize = Dimens.IconSmall,
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = state.artist,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        if (state.isBuffering) {
            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
        }
        MiniPlayerButton(Icons.Filled.SkipPrevious, "Previous", viewModel::previous)
        MiniPlayerButton(
            imageVector = if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
            description = if (state.isPlaying) "Pause" else "Play",
            onClick = viewModel::togglePlayPause,
        )
        MiniPlayerButton(Icons.Filled.SkipNext, "Next", viewModel::next)
        MiniPlayerButton(Icons.Filled.QueueMusic, "Queue", onOpenQueue)

        Text(
            text = formatDurationMs(state.durationMs),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun MiniPlayerButton(
    imageVector: ImageVector,
    description: String,
    onClick: () -> Unit,
) {
    FocusHighlight(cornerRadius = 18.dp) {
        IconButton(onClick = onClick, modifier = Modifier.size(36.dp)) {
            Icon(
                imageVector = imageVector,
                contentDescription = description,
                modifier = Modifier.size(Dimens.Icon),
            )
        }
    }
}
