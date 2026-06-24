package com.example.newsapp.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Full Brief design-system token set (see docs/UI_REFACTOR_PLAN.md §1).
 * Richer than Material3's colorScheme: three text tiers, accent soft/line pair,
 * borders, dividers, chip backgrounds. Provided via [LocalBriefColors] by `BriefTheme`
 * and read through `BriefTheme.colors`.
 */
data class BriefColors(
    val bg: Color,
    val surface: Color,
    val card: Color,
    val border: Color,
    val divider: Color,
    val text: Color,
    val textSec: Color,
    val textTer: Color,
    val accent: Color,
    val accentSoft: Color,
    val accentLine: Color,
    val onAccent: Color,
    val chipBg: Color,
    val placeholder: Color,
    val placeholderText: Color,
    val good: Color,
    val warn: Color,
    val toggleOff: Color,
    val navBg: Color,
    val isDark: Boolean,
)

val LightBriefColors = BriefColors(
    bg = Color(0xFFFAFAF9),
    surface = Color(0xFFFFFFFF),
    card = Color(0xFFFFFFFF),
    border = Color(0xFFE7E5E4),
    divider = Color(0xFFF1EFEC),
    text = Color(0xFF1C1917),
    textSec = Color(0xFF57534E),
    textTer = Color(0xFFA8A29E),
    accent = Color(0xFF047857),
    accentSoft = Color(0xFFECFDF5),
    accentLine = Color(0xFFC7EBD9),
    onAccent = Color(0xFFFFFFFF),
    chipBg = Color(0xFFF4F4F2),
    placeholder = Color(0xFFEEEDE9),
    placeholderText = Color(0xFFB6B0A8),
    good = Color(0xFF047857),
    warn = Color(0xFFB45309),
    toggleOff = Color(0xFFD9D6D1),
    navBg = Color(0xFFFFFFFF),
    isDark = false,
)

val DarkBriefColors = BriefColors(
    bg = Color(0xFF0B0F0E),
    surface = Color(0xFF14181A),
    card = Color(0xFF161B1D),
    border = Color(0xFF272D2F),
    divider = Color(0xFF1E2426),
    text = Color(0xFFF4F4F3),
    textSec = Color(0xFFA6AFAB),
    textTer = Color(0xFF6B746F),
    accent = Color(0xFF10B981),
    accentSoft = Color(0x2210B981), // ~13% alpha emerald
    accentLine = Color(0x4710B981), // ~28% alpha emerald
    onAccent = Color(0xFF04231A),
    chipBg = Color(0xFF1C2123),
    placeholder = Color(0xFF1E2426),
    placeholderText = Color(0xFF48524C),
    good = Color(0xFF34D399),
    warn = Color(0xFFF59E0B),
    toggleOff = Color(0xFF2B3235),
    navBg = Color(0xFF0F1413),
    isDark = true,
)

val LocalBriefColors = staticCompositionLocalOf { LightBriefColors }

/** Accessor object: `BriefTheme.colors.accent`, etc. */
object BriefTheme {
    val colors: BriefColors
        @Composable
        @ReadOnlyComposable
        get() = LocalBriefColors.current
}
