package org.aurral.firetv.data.api

import org.aurral.firetv.data.session.ServerCredentials
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SubsonicAuthTest {

    @Test
    fun `md5Hex matches the well-known abc vector`() {
        assertEquals("900150983cd24fb0d6963f7d28e17f72", SubsonicAuth.md5Hex("abc"))
    }

    @Test
    fun `token matches the documented Subsonic example`() {
        // Subsonic API: md5("sesame" + "c19b2d")
        assertEquals(
            "26719a1196d2a940705a59634eb18eab",
            SubsonicAuth.token(password = "sesame", salt = "c19b2d"),
        )
    }

    @Test
    fun `restBaseUrl appends the rest path once`() {
        assertEquals("http://host:3001/rest/", SubsonicAuth.restBaseUrl("http://host:3001"))
        assertEquals("http://host:3001/rest/", SubsonicAuth.restBaseUrl("http://host:3001/"))
        assertEquals("http://host:3001/rest/", SubsonicAuth.restBaseUrl("http://host:3001/rest"))
        assertEquals("http://host:3001/rest/", SubsonicAuth.restBaseUrl("http://host:3001/rest/"))
        assertEquals("https://music.example.com/sub/rest/", SubsonicAuth.restBaseUrl("https://music.example.com/sub"))
    }

    @Test
    fun `token auth sends a salted hash and no plaintext password`() {
        val credentials = ServerCredentials(
            serverUrl = "http://host",
            username = "joe",
            password = "sesame",
            useTokenAuth = true,
        )

        val params = SubsonicAuth.globalParams(credentials, SESSION_SALT).toMap()

        assertEquals("joe", params["u"])
        assertEquals(SubsonicAuth.API_VERSION, params["v"])
        assertEquals(SubsonicAuth.CLIENT_ID, params["c"])
        assertEquals(SubsonicAuth.JSON_FORMAT, params["f"])
        assertNull(params["p"])
        assertEquals(SESSION_SALT, params["s"])
        assertEquals(SubsonicAuth.md5Hex("sesame$SESSION_SALT"), params["t"])
    }

    @Test
    fun `password auth sends the hex-encoded secret`() {
        val credentials = ServerCredentials(
            serverUrl = "http://host",
            username = "joe",
            password = "sesame",
            useTokenAuth = false,
        )

        val params = SubsonicAuth.globalParams(credentials, SESSION_SALT).toMap()

        assertEquals("enc:736573616d65", params["p"])
        assertNull(params["t"])
        assertFalse(params.containsKey("s"))
    }

    @Test
    fun `signed parameters are stable for a given session salt`() {
        val credentials = ServerCredentials(
            serverUrl = "http://host",
            username = "joe",
            password = "sesame",
            useTokenAuth = true,
        )

        val first = SubsonicAuth.globalParams(credentials, SESSION_SALT)
        val second = SubsonicAuth.globalParams(credentials, SESSION_SALT)

        assertEquals(first, second)
    }

    private companion object {
        const val SESSION_SALT = "c19b2d"
    }
}
