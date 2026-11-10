package org.aurral.firetv.data.api

import kotlinx.serialization.json.Json
import org.aurral.firetv.data.api.dto.SubsonicEnvelope
import org.aurral.firetv.data.mapper.toAlbum
import org.aurral.firetv.data.mapper.toSearchResults
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SubsonicResponseParsingTest {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        coerceInputValues = true
    }

    @Test
    fun `parses a getAlbum response into domain models`() {
        val payload = """
            {"subsonic-response":{"status":"ok","version":"1.16.1","type":"aurral",
            "album":{"id":"al1","name":"Discovery","artist":"Daft Punk","artistId":"ar1",
            "coverArt":"al1","songCount":2,"duration":420,"year":2001,"genre":"Electronic",
            "song":[
              {"id":"s1","title":"One More Time","artist":"Daft Punk","album":"Discovery","albumId":"al1","duration":320,"track":1,"suffix":"flac","bitRate":900},
              {"id":"s2","title":"Digital Love","artist":"Daft Punk","album":"Discovery","albumId":"al1","duration":100,"track":2}
            ]}}}
        """.trimIndent()

        val album = json.decodeFromString<SubsonicEnvelope>(payload).orThrow().album!!.toAlbum()

        assertEquals("al1", album.id)
        assertEquals("Discovery", album.name)
        assertEquals("Daft Punk", album.artist)
        assertEquals(2001, album.year)
        assertEquals(2, album.songs.size)
        assertEquals("One More Time", album.songs.first().title)
        assertEquals(320, album.songs.first().durationSeconds)
        assertEquals("flac", album.songs.first().suffix)
    }

    @Test
    fun `parses a search3 response`() {
        val payload = """
            {"subsonic-response":{"status":"ok","searchResult3":{
              "artist":[{"id":"ar1","name":"Aphex Twin","albumCount":3}],
              "album":[{"id":"al1","name":"Drukqs","artist":"Aphex Twin"}],
              "song":[{"id":"s1","title":"Avril 14th","artist":"Aphex Twin","album":"Drukqs"}]
            }}}
        """.trimIndent()

        val results = json.decodeFromString<SubsonicEnvelope>(payload).orThrow().searchResult3!!.toSearchResults()

        assertEquals(1, results.artists.size)
        assertEquals("Aphex Twin", results.artists.first().name)
        assertEquals(3, results.artists.first().albumCount)
        assertEquals(1, results.albums.size)
        assertEquals(1, results.songs.size)
    }

    @Test
    fun `ignores unknown fields and missing optional values`() {
        val payload = """
            {"subsonic-response":{"status":"ok","surpriseField":42,
            "album":{"id":"al1","name":"X","unknownNested":true}}}
        """.trimIndent()

        val album = json.decodeFromString<SubsonicEnvelope>(payload).orThrow().album!!.toAlbum()

        assertEquals("al1", album.id)
        assertEquals(0, album.songs.size)
        assertTrue(album.coverArtId == null)
    }

    @Test
    fun `failed responses throw a typed exception`() {
        val payload = """
            {"subsonic-response":{"status":"failed","error":{"code":41,"message":"Token authentication failed"}}}
        """.trimIndent()

        val exception = runCatching {
            json.decodeFromString<SubsonicEnvelope>(payload).orThrow()
        }.exceptionOrNull() as SubsonicException

        assertEquals(41, exception.code)
        assertTrue(exception.isTokenAuthFailure)
    }
}
