package org.aurral.firetv.ui.playlists

import org.aurral.firetv.data.model.Playlist

data class PlaylistDetailUiState(
    val playlist: Playlist,
    val errorMessage: String? = null,
    val deleted: Boolean = false,
)
