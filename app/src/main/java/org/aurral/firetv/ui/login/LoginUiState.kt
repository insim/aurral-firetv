package org.aurral.firetv.ui.login

data class LoginUiState(
    val serverUrl: String = "",
    val username: String = "",
    val password: String = "",
    val useTokenAuth: Boolean = true,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
)
