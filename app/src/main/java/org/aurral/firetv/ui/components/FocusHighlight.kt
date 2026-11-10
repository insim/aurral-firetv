package org.aurral.firetv.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.aurral.firetv.ui.theme.Accent

/**
 * Draws an accent ring around [content] while it (or a child) holds focus.
 *
 * Used to give Material buttons a clear D-pad focus state, which Material's
 * default focus indication only hints at.
 */
@Composable
fun FocusHighlight(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    content: @Composable () -> Unit,
) {
    var focused by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .onFocusChanged { focused = it.hasFocus }
            .border(
                width = if (focused) 3.dp else 0.dp,
                color = if (focused) Accent else Color.Transparent,
                shape = RoundedCornerShape(cornerRadius),
            )
            .padding(3.dp),
    ) {
        content()
    }
}
