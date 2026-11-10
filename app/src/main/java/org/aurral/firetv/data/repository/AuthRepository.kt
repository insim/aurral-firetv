package org.aurral.firetv.data.repository

import org.aurral.firetv.data.api.SubsonicApi
import org.aurral.firetv.data.api.orThrow
import org.aurral.firetv.data.session.ServerCredentials
import org.aurral.firetv.data.session.SessionManager
import javax.inject.Inject
import javax.inject.Singleton

/** Validates and persists the user's server credentials. */
@Singleton
class AuthRepository @Inject constructor(
    private val api: SubsonicApi,
    private val sessionManager: SessionManager,
) {

    /** Tests [credentials] against the server and, on success, makes them the active session. */
    suspend fun login(credentials: ServerCredentials): Result<Unit> = runCatching {
        sessionManager.withCredentials(credentials) { api.ping().orThrow() }
        sessionManager.save(credentials)
    }

    suspend fun logout() = sessionManager.logout()

    suspend fun initialize() = sessionManager.initialize()
}
