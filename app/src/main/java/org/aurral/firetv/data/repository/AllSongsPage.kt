package org.aurral.firetv.data.repository

import org.aurral.firetv.data.model.Song

/** One page of the whole library, with how many albums produced it. */
data class AllSongsPage(
    val songs: List<Song>,
    val albumCount: Int,
)
