package org.aurral.firetv.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.aurral.firetv.ui.theme.Accent
import org.aurral.firetv.ui.theme.Dimens
import org.aurral.firetv.ui.theme.OnSurfaceMuted
import org.aurral.firetv.ui.theme.SurfaceVariant

/** One entry of the left navigation rail. */
@Composable
fun NavRailItem(
    destination: AppDestination,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var focused by remember { mutableStateOf(false) }
    val tint = when {
        selected || focused -> Accent
        else -> OnSurfaceMuted
    }
    val background = when {
        focused -> SurfaceVariant
        else -> Color.Transparent
    }

    Column(
        modifier = modifier
            .width(Dimens.NavRailWidth - 8.dp)
            .height(Dimens.NavRailItemHeight)
            .clip(RoundedCornerShape(Dimens.CornerRadius))
            .background(background)
            .onFocusChanged { focused = it.isFocused }
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = destination.icon,
            contentDescription = destination.label,
            tint = tint,
            modifier = Modifier.size(Dimens.Icon),
        )
        Text(
            text = destination.label,
            style = MaterialTheme.typography.bodySmall,
            color = tint,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}
