package org.aurral.firetv.ui.songs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.aurral.firetv.data.model.SongSort
import org.aurral.firetv.ui.common.LoadStateListContent
import org.aurral.firetv.ui.components.PlayActionButtons
import org.aurral.firetv.ui.components.SectionHeader
import org.aurral.firetv.ui.components.SongRow
import org.aurral.firetv.ui.components.SortSelector
import org.aurral.firetv.ui.player.PlayerViewModel
import org.aurral.firetv.ui.theme.Dimens

private const val LOAD_MORE_DISTANCE = 25

@Composable
fun SongsScreen(
    viewModel: SongsViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sort by viewModel.sort.collectAsStateWithLifecycle()
    val canLoadMore by viewModel.canLoadMore.collectAsStateWithLifecycle()
    val playback by playerViewModel.state.collectAsStateWithLifecycle()

    LoadStateListContent(
        state = state,
        onRetry = viewModel::load,
        emptyMessage = "No songs in your library yet",
    ) { songs ->
        val listState = rememberLazyListState()

        if (canLoadMore) {
            val shouldLoadMore by remember(songs) {
                derivedStateOf {
                    val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                        ?: return@derivedStateOf false
                    last >= songs.lastIndex - LOAD_MORE_DISTANCE
                }
            }
            LaunchedEffect(shouldLoadMore) {
                if (shouldLoadMore) viewModel.loadMore()
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = Dimens.ScreenPadding),
        ) {
            item {
                SectionHeader(
                    title = "Songs",
                    subtitle = buildString {
                        append("${songs.size} loaded")
                        if (canLoadMore) append(" · more as you scroll")
                    },
                )
            }
            item {
                SortSelector(
                    options = SongSort.entries,
                    selected = sort,
                    labelOf = { it.label },
                    onSelect = viewModel::setSort,
                    modifier = Modifier.padding(horizontal = Dimens.ScreenPadding, vertical = 4.dp),
                )
            }
            item {
                PlayActionButtons(
                    onPlayAll = viewModel::playAll,
                    onShuffle = viewModel::playShuffled,
                    modifier = Modifier.padding(horizontal = Dimens.ScreenPadding, vertical = 4.dp),
                )
            }
            itemsIndexed(songs, key = { _, song -> song.id }) { index, song ->
                SongRow(
                    title = song.title,
                    subtitle = listOfNotNull(song.artist, song.album.ifBlank { null }).joinToString(" · "),
                    coverArtId = song.coverArtId,
                    durationSeconds = song.durationSeconds,
                    isPlaying = song.id == playback.currentSongId,
                    isStarred = song.starred,
                    onClick = { viewModel.playFrom(index) },
                )
            }
        }
    }
}
