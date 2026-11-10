package org.aurral.firetv.data.session

/** Session data that is persisted in DataStore (the password lives in encrypted storage). */
data class PersistedSettings(
    val serverUrl: String? = null,
    val username: String? = null,
    val useTokenAuth: Boolean = true,
    val streamFormat: String? = StreamSettings.DEFAULT.format,
    val streamMaxBitRate: Int? = StreamSettings.DEFAULT.maxBitRate,
)
