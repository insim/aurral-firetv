package org.aurral.firetv.ui.nowplaying

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import org.aurral.firetv.ui.common.LoadStateContent
import org.aurral.firetv.ui.components.EmptyState
import org.aurral.firetv.ui.components.SectionHeader
import org.aurral.firetv.ui.player.PlayerViewModel
import org.aurral.firetv.ui.theme.Accent

@Composable
fun LyricsScreen(
    songId: String,
    viewModel: LyricsViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val playback by playerViewModel.state.collectAsStateWithLifecycle()

    LoadStateContent(state = state, onRetry = viewModel::load) { lyrics ->
        if (lyrics.isEmpty) {
            EmptyState(message = "No lyrics found for this track.")
            return@LoadStateContent
        }

        val activeIndex = remember(lyrics, playback.positionMs) {
            if (!lyrics.synced) {
                -1
            } else {
                lyrics.lines.indexOfLast { it.startMs != null && it.startMs <= playback.positionMs }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 24.dp, bottom = 48.dp),
        ) {
            item { SectionHeader(title = "Lyrics") }
            itemsIndexed(lyrics.lines) { index, line ->
                val isActive = index == activeIndex
                Text(
                    text = line.text.ifBlank { " " },
                    style = if (isActive) {
                        MaterialTheme.typography.titleLarge
                    } else {
                        MaterialTheme.typography.bodyLarge
                    },
                    color = if (isActive) Accent else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 6.dp),
                )
            }
        }
    }
}
