package org.aurral.firetv.ui.home

import org.aurral.firetv.data.model.Playlist

data class HomeUiState(
    val rails: List<AlbumRail> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
) {
    val heroAlbum = rails.firstOrNull()?.albums?.firstOrNull()
}
