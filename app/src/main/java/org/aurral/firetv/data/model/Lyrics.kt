package org.aurral.firetv.data.model

data class Lyrics(
    val synced: Boolean,
    val lines: List<LyricLine>,
) {
    val isEmpty: Boolean get() = lines.isEmpty()

    companion object {
        val EMPTY = Lyrics(synced = false, lines = emptyList())
    }
}
