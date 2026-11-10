package org.aurral.firetv.data.session

sealed interface SessionState {
    data object Loading : SessionState
    data object LoggedOut : SessionState
    data class LoggedIn(val credentials: ServerCredentials) : SessionState
}
