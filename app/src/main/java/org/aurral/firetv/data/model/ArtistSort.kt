package org.aurral.firetv.data.model

/** Orderings for the artist grid. */
enum class ArtistSort(val label: String) {
    NAME("A–Z"),
    ALBUMS("Most albums"),
    ;

    fun comparator(): Comparator<Artist> = when (this) {
        NAME -> Comparator { a, b -> a.name.compareTo(b.name, ignoreCase = true) }
        ALBUMS -> Comparator { a, b ->
            val byCount = b.albumCount.compareTo(a.albumCount)
            if (byCount != 0) byCount else a.name.compareTo(b.name, ignoreCase = true)
        }
    }
}

fun List<Artist>.orderedBy(sort: ArtistSort): List<Artist> = sortedWith(sort.comparator())
