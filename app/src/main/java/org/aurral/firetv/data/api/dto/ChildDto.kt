package org.aurral.firetv.data.api.dto

import kotlinx.serialization.Serializable

/** A song ("child") as returned by browsing, search and playlist endpoints. */
@Serializable
data class ChildDto(
    val id: String,
    val parent: String? = null,
    val isDir: Boolean = false,
    val title: String? = null,
    val album: String? = null,
    val artist: String? = null,
    val track: Int? = null,
    val year: Int? = null,
    val genre: String? = null,
    val coverArt: String? = null,
    val size: Long? = null,
    val contentType: String? = null,
    val suffix: String? = null,
    val duration: Int? = null,
    val bitRate: Int? = null,
    val path: String? = null,
    val discNumber: Int? = null,
    val created: String? = null,
    val albumId: String? = null,
    val artistId: String? = null,
    val type: String? = null,
    val starred: String? = null,
    val userRating: Int? = null,
)
