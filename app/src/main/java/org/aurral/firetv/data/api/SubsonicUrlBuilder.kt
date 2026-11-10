package org.aurral.firetv.data.api

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.aurral.firetv.data.session.CredentialsProvider
import org.aurral.firetv.data.session.ServerCredentials
import org.aurral.firetv.data.session.StreamSettings
import javax.inject.Inject

/**
 * Builds absolute, authenticated Subsonic URLs for consumers that fetch bytes
 * directly rather than through Retrofit — ExoPlayer (streaming) and Coil (cover art).
 */
class SubsonicUrlBuilder @Inject constructor(
    private val credentialsProvider: CredentialsProvider,
) {

    fun streamUrl(songId: String): String? {
        val credentials = credentialsProvider.credentials() ?: return null
        val params = mutableMapOf("id" to songId)
        val stream: StreamSettings = credentialsProvider.streamSettings()
        stream.format?.takeIf { it.isNotBlank() }?.let { params["format"] = it }
        stream.maxBitRate?.let { params["maxBitRate"] = it.toString() }
        return signedUrl(credentials, "stream.view", params)
    }

    fun coverArtUrl(coverArtId: String, size: Int? = null): String? {
        val credentials = credentialsProvider.credentials() ?: return null
        val params = mutableMapOf("id" to coverArtId)
        size?.let { params["size"] = it.toString() }
        return signedUrl(credentials, "getCoverArt.view", params)
    }

    private fun signedUrl(
        credentials: ServerCredentials,
        endpoint: String,
        params: Map<String, String>,
    ): String? {
        val base: HttpUrl = SubsonicAuth.restBaseUrl(credentials.serverUrl).toHttpUrlOrNull()
            ?: return null
        val builder = base.newBuilder().addPathSegment(endpoint)
        SubsonicAuth.globalParams(credentials, credentialsProvider.authSalt()).forEach { (name, value) ->
            builder.addQueryParameter(name, value)
        }
        params.forEach { (name, value) -> builder.addQueryParameter(name, value) }
        return builder.build().toString()
    }
}
