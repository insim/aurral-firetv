package org.aurral.firetv.ui.common

import org.aurral.firetv.data.api.SubsonicException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/** Maps a failure to something a person can act on. */
fun Throwable.toUserMessage(): String = when (this) {
    is SubsonicException -> userMessage()
    is UnknownHostException -> "Could not find the server. Check the address."
    is ConnectException -> "Could not reach the server. Is it running and on the same network?"
    is SocketTimeoutException -> "The server took too long to respond."
    is HttpException -> "The server returned HTTP ${code()}."
    else -> message ?: "Something went wrong."
}
