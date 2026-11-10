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
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import org.aurral.firetv.ui.player.PlayerViewModel
import org.aurral.firetv.ui.theme.Accent
import org.aurral.firetv.ui.theme.Dimens
import org.aurral.firetv.ui.theme.Surface
import org.aurral.firetv.ui.theme.SurfaceVariant

/**
 * Persistent now-playing strip shown under every screen.
 *
 * The artwork/title area and each control are separate focus targets, so the
 * D-pad reaches the controls rather than being captured by the whole bar.
 */
@Composable
fun MiniPlayerBar(
    onOpenNowPlaying: () -> Unit,
    onOpenQueue: () -> Unit,
    onOpenLyrics: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    if (!state.hasCurrent) return

    val fraction = if (state.durationMs > 0L) {
        (state.positionMs.toFloat() / state.durationMs.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    Column(modifier = modifier.fillMaxWidth().background(Surface)) {
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier.fillMaxWidth().height(2.dp),
            color = Accent,
            trackColor = SurfaceVariant,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.MiniPlayerHeight)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
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
                        modifier = Modifier.size(38.dp),
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
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
            }

            MiniButton(
                imageVector = Icons.Filled.Shuffle,
                description = "Shuffle",
                tint = if (state.shuffleEnabled) Accent else Color.Unspecified,
                onClick = { viewModel.setShuffleEnabled(!state.shuffleEnabled) },
            )
            MiniButton(Icons.Filled.SkipPrevious, "Previous", onClick = viewModel::previous)
            MiniButton(
                imageVector = if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                description = if (state.isPlaying) "Pause" else "Play",
                onClick = viewModel::togglePlayPause,
            )
            MiniButton(Icons.Filled.SkipNext, "Next", onClick = viewModel::next)
            MiniButton(
                imageVector = if (state.repeatMode == Player.REPEAT_MODE_ONE) {
                    Icons.Filled.RepeatOne
                } else {
                    Icons.Filled.Repeat
                },
                description = "Repeat",
                tint = if (state.repeatMode == Player.REPEAT_MODE_OFF) Color.Unspecified else Accent,
                onClick = { viewModel.setRepeatMode(nextRepeatMode(state.repeatMode)) },
            )
            MiniButton(
                imageVector = Icons.Filled.List,
                description = "Lyrics",
                onClick = { state.currentSongId?.let(onOpenLyrics) },
            )
            MiniButton(Icons.Filled.QueueMusic, "Queue", onClick = onOpenQueue)
        }
    }
}

@Composable
private fun MiniButton(
    imageVector: ImageVector,
    description: String,
    onClick: () -> Unit,
    tint: Color = Color.Unspecified,
) {
    FocusHighlight(cornerRadius = 18.dp) {
        IconButton(onClick = onClick, modifier = Modifier.size(34.dp)) {
            Icon(
                imageVector = imageVector,
                contentDescription = description,
                tint = tint,
                modifier = Modifier.size(Dimens.Icon),
            )
        }
    }
}

private fun nextRepeatMode(current: Int): Int = when (current) {
    Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
    Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
    else -> Player.REPEAT_MODE_OFF
}
