package org.aurral.firetv.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/** Type scale tuned for viewing from a couch: slightly larger than phone defaults. */
val AurralTypography = Typography(
    headlineLarge = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.SemiBold),
    headlineMedium = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.SemiBold),
    titleLarge = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Medium),
    titleMedium = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
    bodyLarge = TextStyle(fontSize = 17.sp),
    bodyMedium = TextStyle(fontSize = 15.sp),
    labelLarge = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium),
)
