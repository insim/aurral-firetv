package org.aurral.firetv.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.aurral.firetv.ui.theme.Background
import org.aurral.firetv.ui.theme.Dimens

/** Vertical icon rail for the top-level destinations. */
@Composable
fun AppNavRail(
    currentRoute: String?,
    onSelect: (AppDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(Dimens.NavRailWidth)
            .background(Background)
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AppDestination.entries.forEach { destination ->
            NavRailItem(
                destination = destination,
                selected = currentRoute == destination.route,
                onClick = { onSelect(destination) },
            )
        }
    }
}
