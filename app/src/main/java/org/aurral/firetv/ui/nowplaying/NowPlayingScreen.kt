package org.aurral.firetv.ui.nowplaying

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.Player
import org.aurral.firetv.playback.PLAYBACK_ARTWORK_SIZE
import org.aurral.firetv.ui.common.formatDurationMs
import org.aurral.firetv.ui.components.CoverArt
import org.aurral.firetv.ui.components.EmptyState
import org.aurral.firetv.ui.components.FocusHighlight
import org.aurral.firetv.ui.player.PlayerViewModel
import org.aurral.firetv.ui.theme.Accent
import org.aurral.firetv.ui.theme.Dimens
import org.aurral.firetv.ui.theme.PlayerBackdrop

@Composable
fun NowPlayingScreen(
    onOpenQueue: () -> Unit,
    onOpenLyrics: (String) -> Unit,
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (!state.hasCurrent) {
        EmptyState(message = "Nothing is playing.")
        return
    }

    var dragPosition by remember { mutableStateOf<Float?>(null) }
    val duration = state.durationMs.coerceAtLeast(1L).toFloat()
    val sliderPosition = (dragPosition ?: state.positionMs.toFloat()).coerceIn(0f, duration)

    Box(modifier = Modifier.fillMaxSize().background(PlayerBackdrop)) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Dimens.ScreenPadding, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            CoverArt(
                coverArtId = state.coverArtId,
                modifier = Modifier.size(220.dp),
                requestSizePx = PLAYBACK_ARTWORK_SIZE,
                placeholderIconSize = Dimens.IconLarge,
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = state.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = state.artist,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (state.album.isNotBlank()) {
                    Text(
                        text = state.album,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Slider(
                    value = sliderPosition,
                    onValueChange = { dragPosition = it },
                    onValueChangeFinished = {
                        dragPosition?.let { viewModel.seekTo(it.toLong()) }
                        dragPosition = null
                    },
                    valueRange = 0f..duration,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = formatDurationMs(sliderPosition.toLong()),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = formatDurationMs(state.durationMs),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    PlayerButton(
                        imageVector = Icons.Filled.Shuffle,
                        description = "Shuffle",
                        tint = if (state.shuffleEnabled) Accent else Color.Unspecified,
                        onClick = { viewModel.setShuffleEnabled(!state.shuffleEnabled) },
                    )
                    PlayerButton(
                        imageVector = Icons.Filled.SkipPrevious,
                        description = "Previous",
                        onClick = viewModel::previous,
                    )
                    PlayerButton(
                        imageVector = if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        description = if (state.isPlaying) "Pause" else "Play",
                        large = true,
                        onClick = viewModel::togglePlayPause,
                    )
                    PlayerButton(
                        imageVector = Icons.Filled.SkipNext,
                        description = "Next",
                        onClick = viewModel::next,
                    )
                    PlayerButton(
                        imageVector = if (state.repeatMode == Player.REPEAT_MODE_ONE) {
                            Icons.Filled.RepeatOne
                        } else {
                            Icons.Filled.Repeat
                        },
                        description = "Repeat",
                        tint = if (state.repeatMode == Player.REPEAT_MODE_OFF) {
                            Color.Unspecified
                        } else {
                            Accent
                        },
                        onClick = { viewModel.setRepeatMode(nextRepeatMode(state.repeatMode)) },
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(top = 10.dp),
                ) {
                    FocusHighlight {
                        OutlinedButton(onClick = onOpenQueue) { Text("Queue") }
                    }
                    FocusHighlight {
                        OutlinedButton(
                            onClick = { state.currentSongId?.let(onOpenLyrics) },
                        ) { Text("Lyrics") }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerButton(
    imageVector: ImageVector,
    description: String,
    onClick: () -> Unit,
    tint: Color = Color.Unspecified,
    large: Boolean = false,
) {
    FocusHighlight(cornerRadius = 20.dp) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(if (large) 46.dp else 38.dp),
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = description,
                tint = tint,
                modifier = Modifier.size(if (large) Dimens.IconLarge else Dimens.Icon),
            )
        }
    }
}

private fun nextRepeatMode(current: Int): Int = when (current) {
    Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
    Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
    else -> Player.REPEAT_MODE_OFF
}
