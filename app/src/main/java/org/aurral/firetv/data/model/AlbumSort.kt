package org.aurral.firetv.data.model

/** Orderings for the album grid, mapped onto the server's album-list types. */
enum class AlbumSort(val label: String, val listType: AlbumListType) {
    ALPHABETICAL("A–Z", AlbumListType.ALPHABETICAL_BY_NAME),
    BY_ARTIST("By artist", AlbumListType.ALPHABETICAL_BY_ARTIST),
    NEWEST("Newest", AlbumListType.NEWEST),
    RECENT("Recently played", AlbumListType.RECENT),
}
