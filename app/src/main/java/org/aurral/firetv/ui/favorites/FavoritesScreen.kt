package org.aurral.firetv.ui.favorites

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.aurral.firetv.ui.common.LoadStateContent
import org.aurral.firetv.ui.components.EmptyState
import org.aurral.firetv.ui.components.MediaCard
import org.aurral.firetv.ui.components.MediaCarousel
import org.aurral.firetv.ui.components.PlayActionButtons
import org.aurral.firetv.ui.components.SectionHeader
import org.aurral.firetv.ui.components.SongRow
import org.aurral.firetv.ui.player.PlayerViewModel
import org.aurral.firetv.ui.theme.Dimens

@Composable
fun FavoritesScreen(
    onOpenAlbum: (String) -> Unit,
    onOpenArtist: (String) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel(),
    playerViewModel: PlayerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val playback by playerViewModel.state.collectAsStateWithLifecycle()

    LoadStateContent(state = state, onRetry = viewModel::load) { favorites ->
        if (favorites.isEmpty) {
            EmptyState(message = "No favourites yet. Star artists, albums or tracks to see them here.")
            return@LoadStateContent
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = Dimens.ScreenPadding),
        ) {
            item { SectionHeader(title = "Favourites") }
            if (favorites.songs.isNotEmpty()) {
                item {
                    PlayActionButtons(
                        onPlayAll = { viewModel.playFrom(0) },
                        onShuffle = viewModel::playShuffled,
                        modifier = Modifier.padding(horizontal = Dimens.ScreenPadding, vertical = 6.dp),
                    )
                }
            }

            if (favorites.artists.isNotEmpty()) {
                item {
                    MediaCarousel(
                        title = "Artists",
                        items = favorites.artists,
                        key = { it.id },
                    ) { artist ->
                        MediaCard(
                            title = artist.name,
                            subtitle = "${artist.albumCount} albums",
                            coverArtId = artist.coverArtId,
                            onClick = { onOpenArtist(artist.id) },
                        )
                    }
                }
            }

            if (favorites.albums.isNotEmpty()) {
                item {
                    MediaCarousel(
                        title = "Albums",
                        items = favorites.albums,
                        key = { it.id },
                    ) { album ->
                        MediaCard(
                            title = album.name,
                            subtitle = album.artist,
                            coverArtId = album.coverArtId,
                            onClick = { onOpenAlbum(album.id) },
                        )
                    }
                }
            }

            if (favorites.songs.isNotEmpty()) {
                item { SectionHeader(title = "Tracks") }
                itemsIndexed(favorites.songs, key = { _, song -> song.id }) { index, song ->
                    SongRow(
                        title = song.title,
                        subtitle = song.artist,
                        coverArtId = song.coverArtId,
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
}
