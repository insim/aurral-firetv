package org.aurral.firetv.data.api.interceptor

import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.aurral.firetv.data.api.dto.ErrorDto
import org.aurral.firetv.data.api.dto.SubsonicEnvelope
import org.aurral.firetv.data.api.dto.SubsonicResponseDto

/**
 * Rewrites an unexpected response body into a Subsonic "failed" envelope.
 *
 * Retrofit deserialises straight into a Subsonic envelope, so a non-Subsonic body
 * (an HTML error page, a framework's JSON error, an XML reply when JSON was asked
 * for, an empty body) would otherwise surface as an opaque "field
 * subsonic-response is required", with the real HTTP status discarded.
 *
 * This must not throw: OkHttp rethrows a non-IOException raised inside the
 * interceptor chain from its own dispatcher thread, which kills the process.
 * Substituting a failed envelope keeps the real status and payload visible while
 * travelling through the normal error path.
 *
 * Only installed on the Retrofit client, whose responses are small metadata
 * documents that the JSON converter would buffer in full anyway.
 */
class SubsonicResponseGuardInterceptor(
    private val json: Json,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val body = response.body ?: return response
        val contentType = body.contentType()
        val bytes = runCatching { body.bytes() }.getOrNull() ?: return response
        val payload = bytes.toString(Charsets.UTF_8)

        if (payload.contains(ENVELOPE_KEY)) {
            return response.newBuilder().body(bytes.toResponseBody(contentType)).build()
        }

        val snippet = payload.trim()
            .replace(WHITESPACE, " ")
            .take(MAX_SNIPPET)
            .ifBlank { "<empty body>" }
        val message = "Unexpected response (HTTP ${response.code}, " +
            "${contentType ?: "no content type"}, url ${chain.request().url.redacted()}): $snippet"

        val replacement = json.encodeToString(
            SubsonicEnvelope.serializer(),
            SubsonicEnvelope(
                response = SubsonicResponseDto(
                    status = STATUS_FAILED,
                    error = ErrorDto(code = response.code, message = message),
                ),
            ),
        )

        return response.newBuilder()
            .body(replacement.toResponseBody(JSON_MEDIA_TYPE))
            .build()
    }

    /** Drops credential-bearing query parameters so the message is safe to read or share. */
    private fun HttpUrl.redacted(): String = newBuilder()
        .removeAllQueryParameters(QUERY_TOKEN)
        .removeAllQueryParameters(QUERY_SALT)
        .removeAllQueryParameters(QUERY_PASSWORD)
        .build()
        .toString()

    private companion object {
        const val ENVELOPE_KEY = "\"subsonic-response\""
        const val STATUS_FAILED = "failed"
        const val MAX_SNIPPET = 240
        const val QUERY_TOKEN = "t"
        const val QUERY_SALT = "s"
        const val QUERY_PASSWORD = "p"
        val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
        val WHITESPACE = Regex("\\s+")
    }
}
