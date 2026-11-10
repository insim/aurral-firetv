package org.aurral.firetv.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.aurral.firetv.data.repository.AuthRepository
import org.aurral.firetv.data.session.ServerCredentials
import org.aurral.firetv.data.session.ServerUrlNormalizer
import org.aurral.firetv.ui.common.toUserMessage
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onServerUrlChange(value: String) = updateForm { it.copy(serverUrl = value) }

    fun onUsernameChange(value: String) = updateForm { it.copy(username = value) }

    fun onPasswordChange(value: String) = updateForm { it.copy(password = value) }

    fun onUseTokenAuthChange(value: Boolean) = updateForm { it.copy(useTokenAuth = value) }

    fun submit() {
        val state = _uiState.value
        val validationError = validate(state)
        if (validationError != null) {
            _uiState.update { it.copy(errorMessage = validationError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            val credentials = ServerCredentials(
                serverUrl = ServerUrlNormalizer.normalize(state.serverUrl),
                username = state.username.trim(),
                password = state.password,
                useTokenAuth = state.useTokenAuth,
            )
            authRepository.login(credentials).onFailure { error ->
                _uiState.update { it.copy(isSubmitting = false, errorMessage = error.toUserMessage()) }
            }
        }
    }

    private fun validate(state: LoginUiState): String? = when {
        state.serverUrl.isBlank() -> "Enter the Aurral server address."
        state.username.isBlank() -> "Enter your username."
        state.password.isBlank() -> "Enter your password or app password."
        else -> null
    }

    private fun updateForm(transform: (LoginUiState) -> LoginUiState) {
        _uiState.update { transform(it).copy(errorMessage = null) }
    }
}
