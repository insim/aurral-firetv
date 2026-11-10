package org.aurral.firetv.ui.albums

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.aurral.firetv.data.model.AlbumSort
import org.aurral.firetv.ui.common.LoadStateListContent
import org.aurral.firetv.ui.components.MediaCard
import org.aurral.firetv.ui.components.SectionHeader
import org.aurral.firetv.ui.components.SortMenu
import org.aurral.firetv.ui.theme.Dimens

private const val LOAD_MORE_DISTANCE = 20

@Composable
fun AlbumsScreen(
    onOpenAlbum: (String) -> Unit,
    viewModel: AlbumsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sort by viewModel.sort.collectAsStateWithLifecycle()
    val canLoadMore by viewModel.canLoadMore.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
        SectionHeader(title = "Albums")
        SortMenu(
            options = AlbumSort.entries,
            selected = sort,
            labelOf = { it.label },
            onSelect = viewModel::setSort,
            modifier = Modifier.padding(start = Dimens.ScreenPadding, top = 4.dp),
        )
        LoadStateListContent(
            state = state,
            onRetry = viewModel::load,
            emptyMessage = "No albums in your library yet",
        ) { albums ->
            val gridState = rememberLazyGridState()

            if (canLoadMore) {
                val shouldLoadMore by remember(albums) {
                    derivedStateOf {
                        val last = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                            ?: return@derivedStateOf false
                        last >= albums.lastIndex - LOAD_MORE_DISTANCE
                    }
                }
                LaunchedEffect(shouldLoadMore) {
                    if (shouldLoadMore) viewModel.loadMore()
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 132.dp),
                state = gridState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = Dimens.ScreenPadding, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                items(albums, key = { it.id }) { album ->
                    MediaCard(
                        title = album.name,
                        subtitle = album.artist,
                        coverArtId = album.coverArtId,
                        onClick = { onOpenAlbum(album.id) },
                    )
                }
            }
        }
    }
}
