package org.aurral.firetv.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.aurral.firetv.ui.common.formatBytes
import org.aurral.firetv.ui.components.FocusHighlight
import org.aurral.firetv.ui.components.SectionHeader

private val STREAM_FORMATS = listOf(
    "raw" to "Original (no transcoding)",
    "mp3" to "MP3",
    "opus" to "Opus",
    "aac" to "AAC",
)

private val BITRATES = listOf<Int?>(null, 128, 192, 256, 320)

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(PaddingValues(bottom = 48.dp)),
    ) {
        SectionHeader(title = "Settings")

        SettingRow(label = "Server", value = state.serverUrl)
        SettingRow(label = "Account", value = state.username)
        SettingRow(
            label = "Authentication",
            value = if (state.useTokenAuth) "Token (salted MD5)" else "Legacy password",
        )
        SettingRow(label = "App version", value = state.appVersion)

        SettingGroup(title = "Streaming format") {
            OptionRow(
                options = STREAM_FORMATS.map { it.second },
                selectedIndex = STREAM_FORMATS.indexOfFirst { it.first == state.streamFormat }.coerceAtLeast(0),
                onSelect = { index -> viewModel.setStreamFormat(STREAM_FORMATS[index].first) },
            )
            Caption(
                "Original keeps the file as-is. Other formats ask Aurral (ffmpeg) to transcode.",
            )
        }

        SettingGroup(title = "Maximum bit rate") {
            OptionRow(
                options = BITRATES.map { bitRate -> bitRate?.let { "$it kbps" } ?: "No limit" },
                selectedIndex = BITRATES.indexOf(state.maxBitRate).coerceAtLeast(0),
                onSelect = { index -> viewModel.setMaxBitRate(BITRATES[index]) },
            )
            Caption(
                "A bit rate limit transcodes to MP3. Lower it if playback stutters on Wi-Fi.",
            )
        }

        SettingGroup(title = "Storage") {
            SettingRow(label = "Cover art cache", value = formatBytes(state.imageCacheBytes))
            SettingRow(label = "Audio cache", value = formatBytes(state.mediaCacheBytes))
            FocusHighlight(modifier = Modifier.padding(horizontal = 32.dp, vertical = 12.dp)) {
                OutlinedButton(
                    onClick = viewModel::clearCache,
                    enabled = !state.isClearingCache,
                ) {
                    Text("Clear cache")
                }
            }
            Caption(
                "Caches are sized from the free space on this device and can be cleared at any time.",
            )
        }

        FocusHighlight(modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)) {
            Button(onClick = viewModel::signOut) {
                Text("Sign out")
            }
        }
    }
}

@Composable
private fun SettingRow(label: String, value: String) {
    Column(modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value.ifBlank { "—" },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun SettingGroup(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(modifier = Modifier.padding(top = 20.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp),
        )
        content()
    }
}

@Composable
private fun OptionRow(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(horizontal = 32.dp),
    ) {
        options.forEachIndexed { index, label ->
            FocusHighlight(cornerRadius = 20.dp) {
                if (index == selectedIndex) {
                    Button(onClick = { onSelect(index) }) { Text(label) }
                } else {
                    OutlinedButton(onClick = { onSelect(index) }) { Text(label) }
                }
            }
        }
    }
}

@Composable
private fun Caption(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp),
    )
}
