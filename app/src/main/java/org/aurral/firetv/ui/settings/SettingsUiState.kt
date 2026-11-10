package org.aurral.firetv.ui.settings

data class SettingsUiState(
    val serverUrl: String = "",
    val username: String = "",
    val useTokenAuth: Boolean = true,
    val streamFormat: String = "raw",
    val maxBitRate: Int? = null,
    val appVersion: String = "",
    val imageCacheBytes: Long = 0L,
    val mediaCacheBytes: Long = 0L,
    val isClearingCache: Boolean = false,
)
