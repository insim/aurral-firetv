package org.aurral.firetv.ui.nowplaying

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.text.style.TextAlign
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

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 64.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CoverArt(
            coverArtId = state.coverArtId,
            modifier = Modifier.size(340.dp),
            requestSizePx = PLAYBACK_ARTWORK_SIZE,
            placeholderIconSize = 80.dp,
        )
        Text(
            text = state.title,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 24.dp),
        )
        Text(
            text = state.artist,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (state.album.isNotBlank()) {
            Text(
                text = state.album,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
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
            modifier = Modifier.padding(top = 24.dp).fillMaxWidth(0.7f),
        )
        Row(
            modifier = Modifier.fillMaxWidth(0.7f),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = formatDurationMs(sliderPosition.toLong()),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = formatDurationMs(state.durationMs),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 16.dp),
        ) {
            IconButton(onClick = { viewModel.setShuffleEnabled(!state.shuffleEnabled) }) {
                Icon(
                    imageVector = Icons.Filled.Shuffle,
                    contentDescription = "Shuffle",
                    tint = if (state.shuffleEnabled) Accent else MaterialTheme.colorScheme.onSurfaceVariant,
                )
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
            IconButton(onClick = { viewModel.setRepeatMode(nextRepeatMode(state.repeatMode)) }) {
                Icon(
                    imageVector = if (state.repeatMode == Player.REPEAT_MODE_ONE) {
                        Icons.Filled.RepeatOne
                    } else {
                        Icons.Filled.Repeat
                    },
                    contentDescription = "Repeat",
                    tint = if (state.repeatMode == Player.REPEAT_MODE_OFF) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        Accent
                    },
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(top = 24.dp),
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

private fun nextRepeatMode(current: Int): Int = when (current) {
    Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
    Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
    else -> Player.REPEAT_MODE_OFF
}
