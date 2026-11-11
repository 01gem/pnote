package com.gem.pnote.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val PNoteColorScheme = darkColorScheme(
    primary = OrangePrimary,
    onPrimary = PitchBlack,
    primaryContainer = OrangeContainer,
    onPrimaryContainer = OrangeLight,
    secondary = OrangeVibrant,
    onSecondary = PitchBlack,
    secondaryContainer = CardElevated,
    onSecondaryContainer = OrangeLight,
    tertiary = OrangeLight,
    onTertiary = PitchBlack,
    background = PitchBlack,
    onBackground = TextPrimary,
    surface = ObsidianDark,
    onSurface = TextPrimary,
    surfaceVariant = CardDark,
    onSurfaceVariant = TextSecondary,
    outline = CardBorder,
    outlineVariant = OrangeContainerBorder,
    error = DeleteRed,
    onError = PitchBlack,
)

@Composable
fun PNoteTheme(
    content: @Composable () -> Unit,
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                @Suppress("DEPRECATION")
                window.statusBarColor = PitchBlack.toArgb()
                @Suppress("DEPRECATION")
                window.navigationBarColor = PitchBlack.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = false
                insetsController.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = PNoteColorScheme,
        typography = Typography,
        content = content,
    )
}
