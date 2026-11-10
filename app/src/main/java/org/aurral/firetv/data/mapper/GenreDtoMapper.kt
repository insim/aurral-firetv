package org.aurral.firetv.data.mapper

import org.aurral.firetv.data.api.dto.GenreDto
import org.aurral.firetv.data.model.Genre

fun GenreDto.toGenre(): Genre = Genre(
    name = value,
    songCount = songCount ?: 0,
    albumCount = albumCount ?: 0,
)

fun List<GenreDto>.toGenres(): List<Genre> = map { it.toGenre() }
