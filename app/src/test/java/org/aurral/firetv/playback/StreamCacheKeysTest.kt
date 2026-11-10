package org.aurral.firetv.playback

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class StreamCacheKeysTest {

    @Test
    fun `the same song always maps to the same key`() {
        assertEquals(
            StreamCacheKeys.forStream("song-1", "raw", null),
            StreamCacheKeys.forStream("song-1", "raw", null),
        )
    }

    @Test
    fun `different transcodings of one song get separate keys`() {
        val original = StreamCacheKeys.forStream("song-1", "raw", null)
        val mp3 = StreamCacheKeys.forStream("song-1", "mp3", "192")

        assertNotEquals(original, mp3)
    }

    @Test
    fun `missing format and bit rate fall back to the original`() {
        assertEquals(
            StreamCacheKeys.forStream("song-1", "raw", "0"),
            StreamCacheKeys.forStream("song-1", null, null),
        )
    }

    @Test
    fun `different songs never share a key`() {
        assertNotEquals(
            StreamCacheKeys.forStream("song-1", "raw", null),
            StreamCacheKeys.forStream("song-2", "raw", null),
        )
    }
}
