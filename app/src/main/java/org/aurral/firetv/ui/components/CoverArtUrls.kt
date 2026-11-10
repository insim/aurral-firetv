package org.aurral.firetv.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dagger.hilt.android.EntryPointAccessors
import org.aurral.firetv.di.entrypoint.CoverArtEntryPoint

/** Builds a signed cover-art URL, refreshed only when its inputs change. */
@Composable
fun rememberCoverArtUrl(coverArtId: String?, sizePx: Int): String? {
    val context = LocalContext.current
    val urlBuilder = remember(context) {
        EntryPointAccessors
            .fromApplication(context.applicationContext, CoverArtEntryPoint::class.java)
            .subsonicUrlBuilder()
    }
    return remember(urlBuilder, coverArtId, sizePx) {
        coverArtId
            ?.takeIf { it.isNotBlank() }
            ?.let { urlBuilder.coverArtUrl(it, size = sizePx) }
    }
}
