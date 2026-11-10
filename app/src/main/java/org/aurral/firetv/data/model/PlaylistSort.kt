package org.aurral.firetv.data.model

/** Orderings for the playlist list. */
enum class PlaylistSort(val label: String) {
    NAME("A–Z"),
    SONGS("Most songs"),
    ;

    fun comparator(): Comparator<Playlist> = when (this) {
        NAME -> Comparator { a, b -> a.name.compareTo(b.name, ignoreCase = true) }
        SONGS -> Comparator { a, b ->
            val bySongs = b.songCount.compareTo(a.songCount)
            if (bySongs != 0) bySongs else a.name.compareTo(b.name, ignoreCase = true)
        }
    }
}

fun List<Playlist>.orderedBy(sort: PlaylistSort): List<Playlist> = sortedWith(sort.comparator())
