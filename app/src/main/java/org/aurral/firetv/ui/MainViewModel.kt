package org.aurral.firetv.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.aurral.firetv.data.repository.AuthRepository
import org.aurral.firetv.data.session.SessionManager
import org.aurral.firetv.data.session.SessionState
import org.aurral.firetv.playback.PlaybackController
import javax.inject.Inject

/** App-level state: session plus the one-time connection to the playback service. */
@HiltViewModel
class MainViewModel @Inject constructor(
    sessionManager: SessionManager,
    private val authRepository: AuthRepository,
    playbackController: PlaybackController,
) : ViewModel() {

    val sessionState: StateFlow<SessionState> = sessionManager.state

    init {
        playbackController.connect()
        viewModelScope.launch { authRepository.initialize() }
    }
}
