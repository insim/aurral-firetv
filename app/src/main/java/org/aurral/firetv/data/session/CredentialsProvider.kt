package org.aurral.firetv.data.session

/**
 * Supplies the live session to components that cannot depend on a ViewModel
 * (network interceptors, the URL builder). Reads must be cheap and thread-safe.
 */
interface CredentialsProvider {
    fun credentials(): ServerCredentials?

    fun streamSettings(): StreamSettings

    /** Salt reused for every signed URL in this session. */
    fun authSalt(): String
}
