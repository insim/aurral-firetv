package org.aurral.firetv.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.aurral.firetv.ui.theme.Accent
import org.aurral.firetv.ui.theme.Dimens
import org.aurral.firetv.ui.theme.OnSurfaceMuted

/** A single "Sort" button that opens the ordering choices in a dialog. */
@Composable
fun <T> SortMenu(
    options: List<T>,
    selected: T,
    labelOf: (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Sort by",
) {
    var open by remember { mutableStateOf(false) }

    FocusHighlight {
        OutlinedButton(onClick = { open = true }, modifier = modifier) {
            Icon(
                imageVector = Icons.Filled.Sort,
                contentDescription = null,
                modifier = Modifier.size(Dimens.Icon),
            )
            Text(text = "Sort: ${labelOf(selected)}", modifier = Modifier.padding(start = 6.dp))
        }
    }

    if (open) {
        AlertDialog(
            onDismissRequest = { open = false },
            title = { Text(title) },
            text = {
                Column {
                    options.forEach { option ->
                        val isSelected = option == selected
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    onSelect(option)
                                    open = false
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Icon(
                                imageVector = if (isSelected) {
                                    Icons.Filled.Check
                                } else {
                                    Icons.Outlined.RadioButtonUnchecked
                                },
                                contentDescription = null,
                                tint = if (isSelected) Accent else OnSurfaceMuted,
                                modifier = Modifier.size(Dimens.Icon),
                            )
                            Text(
                                text = labelOf(option),
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isSelected) Accent else MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { open = false }) { Text("Close") }
            },
        )
    }
}
