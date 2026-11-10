package org.aurral.firetv.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val BrandDeep = Color(0xFF241E45)
val BrandGlow = Color(0xFF4B3FA8)
val BrandEdge = Color(0xFF6D5EF8)

/** Soft vertical wash for hero surfaces such as the mix cards. */
val BrandWash = Brush.verticalGradient(listOf(BrandGlow, BrandDeep))

/** Diagonal accent used for calls to action. */
val BrandAccent = Brush.horizontalGradient(listOf(Accent, AccentVariant))

/** Backdrop for the full player, keeping the accent present but subdued. */
val PlayerBackdrop = Brush.verticalGradient(listOf(BrandDeep, Background))
