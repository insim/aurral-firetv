package org.aurral.firetv.data.mapper

import org.aurral.firetv.data.api.dto.ArtistInfo2Dto
import org.aurral.firetv.data.model.ArtistBio

/** Returns null when the server has no usable biography. */
fun ArtistInfo2Dto.toArtistBio(): ArtistBio? {
    val text = biography?.trim()
    if (text.isNullOrEmpty()) return null
    val image = largeImageUrl ?: mediumImageUrl ?: smallImageUrl
    return ArtistBio(biography = text, imageUrl = image)
}
