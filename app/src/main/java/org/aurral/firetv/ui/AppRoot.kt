package org.aurral.firetv.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.aurral.firetv.data.session.SessionState
import org.aurral.firetv.ui.components.LoadingIndicator
import org.aurral.firetv.ui.login.LoginScreen
import org.aurral.firetv.ui.navigation.AppScaffold

/** Chooses between the login flow and the main app based on the stored session. */
@Composable
fun AppRoot(viewModel: MainViewModel = hiltViewModel()) {
    val sessionState by viewModel.sessionState.collectAsStateWithLifecycle()

    when (sessionState) {
        is SessionState.Loading -> LoadingIndicator()
        is SessionState.LoggedOut -> LoginScreen()
        is SessionState.LoggedIn -> AppScaffold()
    }
}
