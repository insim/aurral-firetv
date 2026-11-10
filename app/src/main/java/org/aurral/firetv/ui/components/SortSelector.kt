package org.aurral.firetv.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** A compact row of mutually exclusive sort buttons. */
@Composable
fun <T> SortSelector(
    options: List<T>,
    selected: T,
    labelOf: (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { option ->
            FocusHighlight(cornerRadius = 16.dp) {
                val contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)
                if (option == selected) {
                    Button(onClick = { onSelect(option) }, contentPadding = contentPadding) {
                        Text(labelOf(option), style = MaterialTheme.typography.labelLarge)
                    }
                } else {
                    OutlinedButton(onClick = { onSelect(option) }, contentPadding = contentPadding) {
                        Text(labelOf(option), style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}
