package org.aurral.firetv.ui.genres

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.aurral.firetv.ui.common.LoadStateListContent
import org.aurral.firetv.ui.components.ActionRow
import org.aurral.firetv.ui.components.SectionHeader

@Composable
fun GenresScreen(
    onOpenGenre: (String) -> Unit,
    viewModel: GenresViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(top = 24.dp)) {
        SectionHeader(title = "Genres")
        LoadStateListContent(
            state = state,
            onRetry = viewModel::load,
            emptyMessage = "No genres in your library yet",
        ) { genres ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 8.dp),
            ) {
                items(genres, key = { it.name }) { genre ->
                    ActionRow(
                        title = genre.name,
                        subtitle = "${genre.songCount} songs · ${genre.albumCount} albums",
                        onClick = { onOpenGenre(genre.name) },
                    )
                }
            }
        }
    }
}
