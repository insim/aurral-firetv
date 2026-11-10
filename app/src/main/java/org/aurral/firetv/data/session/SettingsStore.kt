package org.aurral.firetv.data.session

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/** Persists the non-secret parts of the session in DataStore. */
@Singleton
class SettingsStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private object Keys {
        val SERVER_URL = stringPreferencesKey("server_url")
        val USERNAME = stringPreferencesKey("username")
        val USE_TOKEN_AUTH = booleanPreferencesKey("use_token_auth")
        val STREAM_FORMAT = stringPreferencesKey("stream_format")
        val STREAM_MAX_BITRATE = intPreferencesKey("stream_max_bitrate")
    }

    suspend fun load(): PersistedSettings {
        val prefs = context.settingsDataStore.data.first()
        return PersistedSettings(
            serverUrl = prefs[Keys.SERVER_URL],
            username = prefs[Keys.USERNAME],
            useTokenAuth = prefs[Keys.USE_TOKEN_AUTH] ?: true,
            streamFormat = prefs[Keys.STREAM_FORMAT] ?: StreamSettings.DEFAULT.format,
            streamMaxBitRate = prefs[Keys.STREAM_MAX_BITRATE],
        )
    }

    suspend fun saveServer(serverUrl: String, username: String, useTokenAuth: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[Keys.SERVER_URL] = serverUrl
            prefs[Keys.USERNAME] = username
            prefs[Keys.USE_TOKEN_AUTH] = useTokenAuth
        }
    }

    suspend fun saveStreamSettings(settings: StreamSettings) {
        context.settingsDataStore.edit { prefs ->
            settings.format?.let { prefs[Keys.STREAM_FORMAT] = it }
            settings.maxBitRate?.let { prefs[Keys.STREAM_MAX_BITRATE] = it }
        }
    }

    suspend fun clearServer() {
        context.settingsDataStore.edit { prefs ->
            prefs.remove(Keys.SERVER_URL)
            prefs.remove(Keys.USERNAME)
        }
    }
}
