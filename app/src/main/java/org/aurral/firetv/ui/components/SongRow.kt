package org.aurral.firetv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.aurral.firetv.ui.common.formatDuration
import org.aurral.firetv.ui.theme.Accent
import org.aurral.firetv.ui.theme.Dimens
import org.aurral.firetv.ui.theme.SurfaceVariant

/** A focusable track row with cover art and an optional trailing action slot. */
@Composable
fun SongRow(
    title: String,
    subtitle: String?,
    coverArtId: String?,
    durationSeconds: Int,
    isPlaying: Boolean,
    isStarred: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onToggleStar: (() -> Unit)? = null,
    trailing: @Composable (RowScope.() -> Unit)? = null,
) {
    var focused by remember { mutableStateOf(false) }
    val background = when {
        isPlaying -> Accent.copy(alpha = 0.22f)
        focused -> SurfaceVariant
        else -> Color.Transparent
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(background)
            .onFocusChanged { focused = it.isFocused }
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        CoverArt(
            coverArtId = coverArtId,
            modifier = Modifier.size(Dimens.RowCoverArt),
            requestSizePx = 120,
            placeholderIconSize = Dimens.IconSmall,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = if (isPlaying) Accent else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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
        if (onToggleStar != null) {
            RowIconButton(
                imageVector = if (isStarred) Icons.Filled.Star else Icons.Outlined.StarBorder,
                contentDescription = if (isStarred) "Unstar" else "Star",
                onClick = onToggleStar,
            )
        }
        trailing?.invoke(this)
        Text(
            text = formatDuration(durationSeconds),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
