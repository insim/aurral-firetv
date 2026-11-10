package org.aurral.firetv.data.model

/** The `type` values accepted by `getAlbumList2`. */
enum class AlbumListType(val apiValue: String) {
    NEWEST("newest"),
    RECENT("recent"),
    FREQUENT("frequent"),
    RANDOM("random"),
    STARRED("starred"),
    ALPHABETICAL_BY_NAME("alphabeticalByName"),
    ALPHABETICAL_BY_ARTIST("alphabeticalByArtist"),
    ;

    companion object {
        /** Order and labels of the rows shown on the Home screen. */
        val HOME_RAILS: List<Pair<AlbumListType, String>> = listOf(
            NEWEST to "Recently Added",
            RECENT to "Recently Played",
            RANDOM to "Random Albums",
            STARRED to "Starred Albums",
        )
    }
}
