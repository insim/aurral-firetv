package org.aurral.firetv.ui.common

/** Standard loading / data / error wrapper used by every screen. */
sealed interface LoadState<out T> {
    data object Loading : LoadState<Nothing>
    data class Data<T>(val value: T) : LoadState<T>
    data class Error(val message: String) : LoadState<Nothing>
}
