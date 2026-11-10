package org.aurral.firetv.data.api

import org.aurral.firetv.data.session.ServerCredentials
import java.security.MessageDigest
import java.util.Locale

/**
 * Builds the Subsonic request parameters and the REST base URL.
 *
 * Shared by the network interceptor (for metadata calls) and the URL builder
 * (for the signed stream and cover-art URLs handed to ExoPlayer and Coil).
 */
object SubsonicAuth {

    const val API_VERSION = "1.16.1"
    const val CLIENT_ID = "AurralTV"
    const val JSON_FORMAT = "json"

    /** Lower-case hex MD5, as required by the Subsonic token scheme. */
    fun md5Hex(input: String): String {
        val digest = MessageDigest.getInstance("MD5").digest(input.toByteArray(Charsets.UTF_8))
        return digest.joinToString(separator = "") { byte -> "%02x".format(byte) }
    }

    fun token(password: String, salt: String): String = md5Hex(password + salt)

    /**
     * The `u/v/c/f` parameters plus either token (`t`+`s`) or password (`p`) auth.
     *
     * [salt] comes from the session rather than being random per request, so the
     * resulting URLs are stable and cacheable.
     */
    fun globalParams(credentials: ServerCredentials, salt: String): List<Pair<String, String>> {
        val params = mutableListOf(
            "u" to credentials.username,
            "v" to API_VERSION,
            "c" to CLIENT_ID,
            "f" to JSON_FORMAT,
        )
        if (credentials.useTokenAuth) {
            params += "s" to salt
            params += "t" to token(credentials.password, salt)
        } else {
            params += "p" to "enc:" + credentials.password.toByteArray(Charsets.UTF_8).toHex()
        }
        return params
    }

    /** Normalises a user-entered server URL into the `.../rest/` base. */
    fun restBaseUrl(serverUrl: String): String {
        val trimmed = serverUrl.trim().trimEnd('/')
        val withRest = if (trimmed.endsWith("/rest")) trimmed else "$trimmed/rest"
        return "$withRest/"
    }

    private fun ByteArray.toHex(): String {
        val builder = StringBuilder(size * 2)
        for (byte in this) {
            builder.append(String.format(Locale.US, "%02x", byte))
        }
        return builder.toString()
    }
}
