package org.aurral.firetv.data.api.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import org.aurral.firetv.data.api.SubsonicException

/**
 * Turns an unexpected response body into an actionable error.
 *
 * Retrofit deserialises straight into a Subsonic envelope, so a non-Subsonic
 * response (an HTML error page, a framework's JSON error, a redirect to a sign-in
 * page, an empty body) would otherwise surface as an opaque "field
 * subsonic-response is required". Peeking at the body lets the real HTTP status
 * and payload reach the UI instead.
 *
 * Only ever installed on the Retrofit client, whose responses are small metadata
 * documents, so buffering for the peek is cheap.
 */
class SubsonicResponseGuardInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val contentType = response.body?.contentType()?.toString().orEmpty()
        val peek = runCatching { response.peekBody(PEEK_BYTES).string() }.getOrNull().orEmpty()

        if (!peek.contains(ENVELOPE_KEY)) {
            val snippet = peek.trim()
                .replace(WHITESPACE, " ")
                .take(MAX_SNIPPET)
                .ifBlank { "<empty body>" }
            val message = "Unexpected response " +
                "(HTTP ${response.code}, ${contentType.ifBlank { "no content type" }}, " +
                "url ${chain.request().url}): $snippet"
            response.close()
            throw SubsonicException(code = response.code, serverMessage = message)
        }

        return response
    }

    private companion object {
        const val PEEK_BYTES = 64L * 1024
        const val ENVELOPE_KEY = "\"subsonic-response\""
        const val MAX_SNIPPET = 240
        val WHITESPACE = Regex("\\s+")
    }
}
