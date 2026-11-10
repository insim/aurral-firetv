package org.aurral.firetv.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AurralColorScheme = darkColorScheme(
    primary = Accent,
    onPrimary = Color.White,
    primaryContainer = AccentVariant,
    onPrimaryContainer = Background,
    secondary = AccentVariant,
    onSecondary = Background,
    background = Background,
    onBackground = OnSurface,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceMuted,
    outline = SurfaceVariant,
)

/** Dark-first TV theme with a single violet accent. */
@Composable
fun AurralTvTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AurralColorScheme,
        typography = AurralTypography,
        content = content,
    )
}
