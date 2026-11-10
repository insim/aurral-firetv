package org.aurral.firetv.ui.genres

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.aurral.firetv.ui.common.LoadStateListContent
import org.aurral.firetv.ui.components.SectionHeader
import org.aurral.firetv.ui.components.SongRow
import org.aurral.firetv.ui.player.PlayerViewModel

@Composable
fun GenreDetailScreen(
    genreName: String,
    viewModel: GenreDetailViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val playback by playerViewModel.state.collectAsStateWithLifecycle()

    LoadStateListContent(
        state = state,
        onRetry = viewModel::load,
        emptyMessage = "No songs in this genre",
    ) { songs ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp),
        ) {
            item { SectionHeader(title = viewModel.genreName, subtitle = "${songs.size} songs") }
            itemsIndexed(songs, key = { _, song -> song.id }) { index, song ->
                SongRow(
                    position = index + 1,
                    title = song.title,
                    subtitle = song.artist,
                    durationSeconds = song.durationSeconds,
                    isPlaying = song.id == playback.currentSongId,
                    isStarred = song.starred,
                    onClick = { viewModel.playFrom(index) },
                    onToggleStar = { viewModel.toggleSongStar(song) },
                )
            }
        }
    }
}
