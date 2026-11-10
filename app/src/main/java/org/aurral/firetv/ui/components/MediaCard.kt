package org.aurral.firetv.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Cover art with a title and optional subtitle, the primary browsing tile. */
@Composable
fun MediaCard(
    title: String,
    subtitle: String?,
    coverArtId: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 176.dp,
) {
    Column(modifier = modifier.width(width)) {
        FocusableCard(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 10.dp,
        ) {
            CoverArt(
                coverArtId = coverArtId,
                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                requestSizePx = 400,
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 10.dp),
        )
        if (!subtitle.isNullOrBlank()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
