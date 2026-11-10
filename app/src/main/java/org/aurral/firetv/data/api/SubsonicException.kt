package org.aurral.firetv.data.api

/** A non-ok Subsonic response, carrying the numeric error code and server message. */
class SubsonicException(
    val code: Int,
    val serverMessage: String? = null,
) : Exception(serverMessage ?: "Subsonic error $code") {

    val isTokenAuthFailure: Boolean get() = code == 41

    fun userMessage(): String = when (code) {
        0 -> "The server rejected the request."
        10 -> "A required parameter was missing."
        20, 30 -> "This app and the server use incompatible API versions."
        40 -> "Wrong username or password."
        41 -> "Token authentication failed. Enable \"legacy password\" or make sure the Aurral account has a local password set."
        50 -> serverMessage ?: "You are not allowed to do that (flows cannot be edited)."
        60 -> "The server trial period is over."
        70 -> "The requested item was not found."
        else -> serverMessage ?: "Server error $code."
    }
}
