package org.aurral.firetv.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.aurral.firetv.ui.components.EmptyState
import org.aurral.firetv.ui.components.ErrorState
import org.aurral.firetv.ui.components.LoadingIndicator

/** Renders [state], delegating the data branch to [content]. */
@Composable
fun <T> LoadStateContent(
    state: LoadState<T>,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit,
) {
    when (state) {
        is LoadState.Loading -> LoadingIndicator(modifier)
        is LoadState.Error -> ErrorState(message = state.message, onRetry = onRetry, modifier = modifier)
        is LoadState.Data -> content(state.value)
    }
}

/** Convenience for screens whose data can be "present but empty". */
@Composable
fun <T> LoadStateListContent(
    state: LoadState<List<T>>,
    onRetry: (() -> Unit)? = null,
    emptyMessage: String = "Nothing here yet",
    content: @Composable (List<T>) -> Unit,
) {
    when (state) {
        is LoadState.Loading -> LoadingIndicator()
        is LoadState.Error -> ErrorState(message = state.message, onRetry = onRetry)
        is LoadState.Data -> if (state.value.isEmpty()) {
            EmptyState(message = emptyMessage)
        } else {
            content(state.value)
        }
    }
}
