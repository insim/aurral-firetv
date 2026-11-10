package org.aurral.firetv.ui.home

import org.aurral.firetv.data.model.Album

/** One titled row of albums on the Home screen. */
data class AlbumRail(
    val title: String,
    val albums: List<Album>,
)
