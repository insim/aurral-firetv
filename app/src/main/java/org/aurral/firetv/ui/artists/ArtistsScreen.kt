package org.aurral.firetv.ui.artists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.aurral.firetv.ui.common.LoadStateListContent
import org.aurral.firetv.ui.components.MediaCard
import org.aurral.firetv.ui.components.SectionHeader
import org.aurral.firetv.ui.theme.Dimens

@Composable
fun ArtistsScreen(
    onOpenArtist: (String) -> Unit,
    viewModel: ArtistsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(top = 24.dp)) {
        SectionHeader(title = "Artists")
        LoadStateListContent(
            state = state,
            onRetry = viewModel::load,
            emptyMessage = "No artists in your library yet",
        ) { artists ->
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 132.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = Dimens.ScreenPadding, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                items(artists, key = { it.id }) { artist ->
                    MediaCard(
                        title = artist.name,
                        subtitle = "${artist.albumCount} albums",
                        coverArtId = artist.coverArtId,
                        onClick = { onOpenArtist(artist.id) },
                    )
                }
            }
        }
    }
}
