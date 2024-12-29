package org.juseni.daytoday.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily

import org.jetbrains.compose.resources.Font
import org.juseni.daytoday.resources.Res
import org.juseni.daytoday.resources.assistant_bold
import org.juseni.daytoday.resources.assistant_regular

@Composable
fun BodyFontFamily() = FontFamily(
    Font(resource = Res.font.assistant_bold)
)

@Composable
fun DisplayFontFamily() = FontFamily(
    Font(resource = Res.font.assistant_regular)
)

// Default Material 3 typography values
val baseline = Typography()

@Composable
fun AppTypography() = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = DisplayFontFamily()),
    displayMedium = baseline.displayMedium.copy(fontFamily = DisplayFontFamily()),
    displaySmall = baseline.displaySmall.copy(fontFamily = DisplayFontFamily()),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = DisplayFontFamily()),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = DisplayFontFamily()),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = DisplayFontFamily()),
    titleLarge = baseline.titleLarge.copy(fontFamily = DisplayFontFamily()),
    titleMedium = baseline.titleMedium.copy(fontFamily = DisplayFontFamily()),
    titleSmall = baseline.titleSmall.copy(fontFamily = DisplayFontFamily()),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = BodyFontFamily()),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = BodyFontFamily()),
    bodySmall = baseline.bodySmall.copy(fontFamily = BodyFontFamily()),
    labelLarge = baseline.labelLarge.copy(fontFamily = BodyFontFamily()),
    labelMedium = baseline.labelMedium.copy(fontFamily = BodyFontFamily()),
    labelSmall = baseline.labelSmall.copy(fontFamily = BodyFontFamily()),
)

