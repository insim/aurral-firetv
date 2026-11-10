package org.aurral.firetv.data.model

/** Client-side orderings for track lists. */
enum class SongSort(val label: String) {
    TITLE("Title"),
    ARTIST("Artist"),
    ALBUM("Album"),
    LONGEST("Longest"),
    ;

    fun comparator(): Comparator<Song> = when (this) {
        TITLE -> Comparator { a, b -> a.title.compareTo(b.title, ignoreCase = true) }
        ARTIST -> Comparator { a, b ->
            val byArtist = a.artist.compareTo(b.artist, ignoreCase = true)
            if (byArtist != 0) byArtist else a.title.compareTo(b.title, ignoreCase = true)
        }
        ALBUM -> Comparator { a, b ->
            val byAlbum = a.album.compareTo(b.album, ignoreCase = true)
            when {
                byAlbum != 0 -> byAlbum
                (a.discNumber ?: 0) != (b.discNumber ?: 0) -> (a.discNumber ?: 0).compareTo(b.discNumber ?: 0)
                else -> (a.trackNumber ?: 0).compareTo(b.trackNumber ?: 0)
            }
        }
        LONGEST -> Comparator { a, b -> b.durationSeconds.compareTo(a.durationSeconds) }
    }
}

fun List<Song>.orderedBy(sort: SongSort): List<Song> = sortedWith(sort.comparator())
