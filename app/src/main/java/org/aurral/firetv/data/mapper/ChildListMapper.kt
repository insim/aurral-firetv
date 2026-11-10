package org.aurral.firetv.data.mapper

import org.aurral.firetv.data.api.dto.ChildDto
import org.aurral.firetv.data.model.Song

fun List<ChildDto>.toSongs(): List<Song> = map { it.toSong() }
