package org.aurral.firetv.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.aurral.firetv.ui.theme.Dimens

/** The "Play all / Shuffle" pair used on every list header. */
@Composable
fun PlayActionButtons(
    onPlayAll: () -> Unit,
    onShuffle: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        FocusHighlight {
            Button(onClick = onPlayAll, enabled = enabled) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(Dimens.Icon),
                )
                Text(text = "Play all", modifier = Modifier.padding(start = 6.dp))
            }
        }
        FocusHighlight {
            OutlinedButton(onClick = onShuffle, enabled = enabled) {
                Icon(
                    imageVector = Icons.Filled.Shuffle,
                    contentDescription = null,
                    modifier = Modifier.size(Dimens.Icon),
                )
                Text(text = "Shuffle", modifier = Modifier.padding(start = 6.dp))
            }
        }
    }
}
