package com.skystone1000.briefly.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Root theme for the Brief design system. Provides the full [BriefColors] token set via
 * [LocalBriefColors] (read through `BriefTheme.colors`) and a minimal Material3
 * colorScheme so ripple/selection defaults and any stray Material components stay coherent.
 */
@Composable
fun BriefTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkBriefColors else LightBriefColors

    val scheme = if (darkTheme) {
        darkColorScheme(
            background = colors.bg,
            surface = colors.card,
            primary = colors.accent,
            onPrimary = colors.onAccent,
            error = colors.warn,
            onBackground = colors.text,
            onSurface = colors.text,
        )
    } else {
        lightColorScheme(
            background = colors.bg,
            surface = colors.card,
            primary = colors.accent,
            onPrimary = colors.onAccent,
            error = colors.warn,
            onBackground = colors.text,
            onSurface = colors.text,
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.bg.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalBriefColors provides colors) {
        MaterialTheme(
            colorScheme = scheme,
            typography = BriefTypography,
            content = content,
        )
    }
}
