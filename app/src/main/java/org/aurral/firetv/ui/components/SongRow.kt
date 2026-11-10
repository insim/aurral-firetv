package org.aurral.firetv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.aurral.firetv.ui.common.formatDuration
import org.aurral.firetv.ui.theme.Accent
import org.aurral.firetv.ui.theme.SurfaceVariant

/** A focusable track row for vertical track lists. */
@Composable
fun SongRow(
    position: Int,
    title: String,
    subtitle: String?,
    durationSeconds: Int,
    isPlaying: Boolean,
    isStarred: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onToggleStar: (() -> Unit)? = null,
    onRemove: (() -> Unit)? = null,
) {
    var focused by remember { mutableStateOf(false) }
    val background = when {
        isPlaying -> Accent.copy(alpha = 0.22f)
        focused -> SurfaceVariant
        else -> androidx.compose.ui.graphics.Color.Transparent
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .onFocusChanged { focused = it.isFocused }
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = position.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(28.dp),
        )
        if (isPlaying) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Now playing",
                tint = Accent,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
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
            IconButton(onClick = onToggleStar) {
                Icon(
                    imageVector = if (isStarred) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = if (isStarred) "Unstar" else "Star",
                    tint = if (isStarred) Accent else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (onRemove != null) {
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Filled.DeleteOutline,
                    contentDescription = "Remove from playlist",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Text(
            text = formatDuration(durationSeconds),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
