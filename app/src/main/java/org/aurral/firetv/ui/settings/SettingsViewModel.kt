package org.aurral.firetv.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.aurral.firetv.BuildConfig
import org.aurral.firetv.data.cache.CacheManager
import org.aurral.firetv.data.repository.AuthRepository
import org.aurral.firetv.data.session.SessionManager
import org.aurral.firetv.data.session.StreamSettings
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val authRepository: AuthRepository,
    private val cacheManager: CacheManager,
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        refresh()
        refreshCacheSizes()
    }

    fun refresh() {
        val credentials = sessionManager.credentials()
        val stream = sessionManager.streamSettings()
        _state.update {
            it.copy(
                serverUrl = credentials?.serverUrl.orEmpty(),
                username = credentials?.username.orEmpty(),
                useTokenAuth = credentials?.useTokenAuth ?: true,
                streamFormat = stream.format ?: "raw",
                maxBitRate = stream.maxBitRate,
                appVersion = BuildConfig.VERSION_NAME,
            )
        }
    }

    fun refreshCacheSizes() {
        viewModelScope.launch {
            runCatching { cacheManager.sizes() }.onSuccess { sizes ->
                _state.update {
                    it.copy(imageCacheBytes = sizes.imageBytes, mediaCacheBytes = sizes.mediaBytes)
                }
            }
        }
    }

    fun setStreamFormat(format: String) {
        viewModelScope.launch {
            sessionManager.updateStreamSettings(sessionManager.streamSettings().copy(format = format))
            refresh()
        }
    }

    fun setMaxBitRate(bitRate: Int?) {
        viewModelScope.launch {
            sessionManager.updateStreamSettings(sessionManager.streamSettings().copy(maxBitRate = bitRate))
            refresh()
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            _state.update { it.copy(isClearingCache = true) }
            cacheManager.clear()
            _state.update { it.copy(isClearingCache = false) }
            refreshCacheSizes()
        }
    }

    fun signOut() {
        viewModelScope.launch { authRepository.logout() }
    }
}
