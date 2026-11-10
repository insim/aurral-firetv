package org.aurral.firetv.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** A titled, horizontally scrolling row of cards for TV. */
@Composable
fun <T> MediaCarousel(
    title: String,
    items: List<T>,
    key: (T) -> Any,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    itemContent: @Composable (T) -> Unit,
) {
    if (items.isEmpty()) return
    Column(modifier = modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        SectionHeader(title = title, subtitle = subtitle)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(items = items, key = key) { item -> itemContent(item) }
        }
    }
}
