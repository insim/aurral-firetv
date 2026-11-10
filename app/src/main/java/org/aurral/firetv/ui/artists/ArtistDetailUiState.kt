package org.aurral.firetv.ui.artists

import org.aurral.firetv.data.model.Artist
import org.aurral.firetv.data.model.Song

data class ArtistDetailUiState(
    val artist: Artist,
    val topSongs: List<Song>,
)
