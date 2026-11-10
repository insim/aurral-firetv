package org.aurral.firetv.data.api.interceptor

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import org.aurral.firetv.data.api.SubsonicAuth
import org.aurral.firetv.data.session.CredentialsProvider

/**
 * Rewrites every request onto the configured Aurral server and appends the
 * Subsonic global parameters (`u/v/c/f`) plus authentication.
 *
 * Retrofit is built against a placeholder host; the real host and `/rest` path
 * prefix are resolved here from the live session, so changing the server needs
 * no client rebuild.
 */
class SubsonicRequestInterceptor(
    private val credentialsProvider: CredentialsProvider,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val credentials = credentialsProvider.credentials() ?: return chain.proceed(original)
        val restBase = SubsonicAuth.restBaseUrl(credentials.serverUrl).toHttpUrlOrNull()
            ?: return chain.proceed(original)

        val builder = original.url.newBuilder()
            .scheme(restBase.scheme)
            .host(restBase.host)
            .port(restBase.port)

        val basePath = restBase.encodedPath.trimEnd('/')
        val endpointPath = original.url.encodedPath.trimStart('/')
        builder.encodedPath("$basePath/$endpointPath")

        SubsonicAuth.globalParams(credentials, credentialsProvider.authSalt()).forEach { (name, value) ->
            builder.addQueryParameter(name, value)
        }

        return chain.proceed(original.newBuilder().url(builder.build()).build())
    }
}

/** Placeholder base used to build Retrofit; [SubsonicRequestInterceptor] replaces it. */
internal val PLACEHOLDER_BASE_URL: HttpUrl = "http://aurral.invalid/".toHttpUrlOrNull()!!
