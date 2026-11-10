package org.aurral.firetv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.aurral.firetv.playback.artworkCacheKey
import org.aurral.firetv.ui.theme.Dimens
import org.aurral.firetv.ui.theme.OnSurfaceMuted
import org.aurral.firetv.ui.theme.SurfaceVariant

/**
 * Square cover art with a graceful placeholder, cached by [coverArtId] so the
 * per-request auth salt does not defeat Coil's cache.
 */
@Composable
fun CoverArt(
    coverArtId: String?,
    modifier: Modifier = Modifier,
    requestSizePx: Int = 400,
    placeholderIconSize: Dp = Dimens.Icon,
) {
    val url = rememberCoverArtUrl(coverArtId, requestSizePx)
    val cacheKey = coverArtId?.let { artworkCacheKey(it, requestSizePx) }
    val shape = RoundedCornerShape(Dimens.CornerRadius)

    Box(
        modifier = modifier.clip(shape).background(SurfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        if (url != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .memoryCacheKey(cacheKey)
                    .diskCacheKey(cacheKey)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            Icon(
                imageVector = Icons.Filled.MusicNote,
                contentDescription = null,
                tint = OnSurfaceMuted,
                modifier = Modifier.size(placeholderIconSize),
            )
        }
    }
}
