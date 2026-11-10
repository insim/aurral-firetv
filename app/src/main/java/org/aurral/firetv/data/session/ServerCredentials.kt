package org.aurral.firetv.data.session

/** The credentials needed to talk to an Aurral Subsonic endpoint. */
data class ServerCredentials(
    val serverUrl: String,
    val username: String,
    val password: String,
    val useTokenAuth: Boolean,
)
