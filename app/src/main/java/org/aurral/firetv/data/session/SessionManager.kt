package org.aurral.firetv.data.session

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single source of truth for the active session.
 *
 * Keeps the credentials in memory for synchronous reads by network interceptors
 * while persisting them through [SettingsStore] (non-secret) and
 * [SecurePasswordStore] (password — disk and Keystore work, so always on IO).
 */
@Singleton
class SessionManager @Inject constructor(
    private val settingsStore: SettingsStore,
    private val securePasswordStore: SecurePasswordStore,
) : CredentialsProvider {

    @Volatile
    private var currentCredentials: ServerCredentials? = null

    @Volatile
    private var currentStreamSettings: StreamSettings = StreamSettings.DEFAULT

    @Volatile
    private var currentSalt: String = AuthSalt.generate()

    private val _state = MutableStateFlow<SessionState>(SessionState.Loading)
    val state: StateFlow<SessionState> = _state.asStateFlow()

    override fun credentials(): ServerCredentials? = currentCredentials

    override fun streamSettings(): StreamSettings = currentStreamSettings

    override fun authSalt(): String = currentSalt

    suspend fun initialize() {
        val credentials = withContext(Dispatchers.IO) {
            val persisted = settingsStore.load()
            currentStreamSettings = StreamSettings(persisted.streamFormat, persisted.streamMaxBitRate)
            buildCredentials(persisted, securePasswordStore.load())
        }
        currentCredentials = credentials
        _state.value = credentials?.let(SessionState::LoggedIn) ?: SessionState.LoggedOut
    }

    /**
     * Runs [block] with [credentials] active (without persisting them), then
     * restores the previous session. Used to validate credentials before login.
     */
    suspend fun <T> withCredentials(credentials: ServerCredentials, block: suspend () -> T): T {
        val previous = currentCredentials
        currentCredentials = credentials
        try {
            return block()
        } finally {
            currentCredentials = previous
        }
    }

    suspend fun save(credentials: ServerCredentials) {
        withContext(Dispatchers.IO) {
            settingsStore.saveServer(credentials.serverUrl, credentials.username, credentials.useTokenAuth)
            securePasswordStore.save(credentials.password)
        }
        currentSalt = AuthSalt.generate()
        currentCredentials = credentials
        _state.value = SessionState.LoggedIn(credentials)
    }

    suspend fun updateStreamSettings(settings: StreamSettings) {
        withContext(Dispatchers.IO) { settingsStore.saveStreamSettings(settings) }
        currentStreamSettings = settings
    }

    suspend fun logout() {
        withContext(Dispatchers.IO) {
            settingsStore.clearServer()
            securePasswordStore.clear()
        }
        currentCredentials = null
        currentStreamSettings = StreamSettings.DEFAULT
        currentSalt = AuthSalt.generate()
        _state.value = SessionState.LoggedOut
    }

    private fun buildCredentials(persisted: PersistedSettings, password: String?): ServerCredentials? {
        val serverUrl = persisted.serverUrl?.takeIf { it.isNotBlank() } ?: return null
        val username = persisted.username?.takeIf { it.isNotBlank() } ?: return null
        val secret = password?.takeIf { it.isNotEmpty() } ?: return null
        return ServerCredentials(serverUrl, username, secret, persisted.useTokenAuth)
    }
}
