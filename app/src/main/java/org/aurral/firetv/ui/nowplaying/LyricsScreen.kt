package org.aurral.firetv.ui.nowplaying

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.aurral.firetv.ui.common.LoadStateContent
import org.aurral.firetv.ui.components.EmptyState
import org.aurral.firetv.ui.components.SectionHeader
import org.aurral.firetv.ui.player.PlayerViewModel
import org.aurral.firetv.ui.theme.Accent
import org.aurral.firetv.ui.theme.Dimens

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

        val listState = rememberLazyListState()
        val activeIndex = remember(lyrics, playback.positionMs) {
            if (!lyrics.synced) {
                -1
            } else {
                lyrics.lines.indexOfLast { it.startMs != null && it.startMs <= playback.positionMs }
            }
        }

        LaunchedEffect(activeIndex) {
            if (activeIndex >= 0) listState.animateScrollToItem(activeIndex)
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = Dimens.ScreenPadding),
        ) {
            item {
                SectionHeader(
                    title = "Lyrics",
                    subtitle = if (lyrics.synced) "Synced" else null,
                )
            }
            itemsIndexed(lyrics.lines) { index, line ->
                val isActive = index == activeIndex
                Text(
                    text = line.text.ifBlank { " " },
                    style = if (isActive) {
                        MaterialTheme.typography.titleMedium
                    } else {
                        MaterialTheme.typography.bodyLarge
                    },
                    color = if (isActive) Accent else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = Dimens.ScreenPadding, vertical = 4.dp),
                )
            }
        }
    }
}
