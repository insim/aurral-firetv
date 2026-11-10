package org.aurral.firetv.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.aurral.firetv.ui.theme.Accent
import org.aurral.firetv.ui.theme.SurfaceVariant

/**
 * D-pad friendly clickable container: scales up and gains an accent border while
 * focused, so the current selection is always obvious on a TV.
 */
@Composable
fun FocusableCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 12.dp,
    backgroundColor: Color = SurfaceVariant,
    focusedScale: Float = 1.06f,
    onFocused: (Boolean) -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
) {
    var focused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (focused) focusedScale else 1f, label = "focusScale")
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .scale(scale)
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = if (focused) 3.dp else 0.dp,
                color = if (focused) Accent else Color.Transparent,
                shape = shape,
            )
            .onFocusChanged {
                focused = it.isFocused
                onFocused(it.isFocused)
            }
            .clickable(onClick = onClick),
        content = content,
    )
}
