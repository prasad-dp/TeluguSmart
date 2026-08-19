package com.example.ui.keyboard

import androidx.compose.ui.graphics.Color
import com.example.data.KeyboardThemeType

data class KeyboardPalette(
    val background: Color,
    val surface: Color,
    val keyBackground: Color,
    val keyText: Color,
    val keySecondaryText: Color,
    val specialKeyBackground: Color,
    val specialKeyText: Color,
    val accent: Color,
    val accentText: Color,
    val suggestionBackground: Color,
    val suggestionText: Color,
    val border: Color
)

object KeyboardThemeColors {
    fun getPalette(type: KeyboardThemeType): KeyboardPalette {
        return when (type) {
            KeyboardThemeType.PITCH_BLACK -> KeyboardPalette(
                background = Color(0xFF000000),
                surface = Color(0xFF111113),
                keyBackground = Color(0xFF18181B),
                keyText = Color(0xFFFFFFFF),
                keySecondaryText = Color(0xFFA1A1AA),
                specialKeyBackground = Color(0xFF09090B),
                specialKeyText = Color(0xFF38BDF8),
                accent = Color(0xFF0284C7),
                accentText = Color(0xFFFFFFFF),
                suggestionBackground = Color(0xFF09090B),
                suggestionText = Color(0xFFF4F4F5),
                border = Color(0x3338BDF8)
            )
            KeyboardThemeType.MIDNIGHT_SLATE -> KeyboardPalette(
                background = Color(0xFF0F172A),
                surface = Color(0xFF1E293B),
                keyBackground = Color(0xFF273549),
                keyText = Color(0xFFF8FAFC),
                keySecondaryText = Color(0xFF94A3B8),
                specialKeyBackground = Color(0xFF182234),
                specialKeyText = Color(0xFF60A5FA),
                accent = Color(0xFF2563EB),
                accentText = Color(0xFFFFFFFF),
                suggestionBackground = Color(0xFF1E293B),
                suggestionText = Color(0xFFF8FAFC),
                border = Color(0x3360A5FA)
            )
            KeyboardThemeType.CLEAN_LIGHT -> KeyboardPalette(
                background = Color(0xFFF1F5F9),
                surface = Color(0xFFFFFFFF),
                keyBackground = Color(0xFFFFFFFF),
                keyText = Color(0xFF0F172A),
                keySecondaryText = Color(0xFF64748B),
                specialKeyBackground = Color(0xFFE2E8F0),
                specialKeyText = Color(0xFF2563EB),
                accent = Color(0xFF2563EB),
                accentText = Color(0xFFFFFFFF),
                suggestionBackground = Color(0xFFFFFFFF),
                suggestionText = Color(0xFF0F172A),
                border = Color(0x1F000000)
            )
            KeyboardThemeType.DESH_ROYAL_BLUE -> KeyboardPalette(
                background = Color(0xFF0B192C),
                surface = Color(0xFF1E3E62),
                keyBackground = Color(0xFF1E3E62),
                keyText = Color(0xFFF8FAFC),
                keySecondaryText = Color(0xFF93C5FD),
                specialKeyBackground = Color(0xFF001F3F),
                specialKeyText = Color(0xFF60A5FA),
                accent = Color(0xFF0066FF),
                accentText = Color(0xFFFFFFFF),
                suggestionBackground = Color(0xFF001F3F),
                suggestionText = Color(0xFFF8FAFC),
                border = Color(0x4460A5FA)
            )
            KeyboardThemeType.SAFFRON_ORANGE -> KeyboardPalette(
                background = Color(0xFF1C0D02),
                surface = Color(0xFF2E1504),
                keyBackground = Color(0xFF451E06),
                keyText = Color(0xFFFFF7ED),
                keySecondaryText = Color(0xFFFDBA74),
                specialKeyBackground = Color(0xFF331705),
                specialKeyText = Color(0xFFFB923C),
                accent = Color(0xFFEA580C),
                accentText = Color(0xFFFFFFFF),
                suggestionBackground = Color(0xFF2E1504),
                suggestionText = Color(0xFFFFF7ED),
                border = Color(0x44FB923C)
            )
            KeyboardThemeType.EMERALD_GREEN -> KeyboardPalette(
                background = Color(0xFF051E14),
                surface = Color(0xFF0B3323),
                keyBackground = Color(0xFF124B34),
                keyText = Color(0xFFF0FDF4),
                keySecondaryText = Color(0xFF86EFAC),
                specialKeyBackground = Color(0xFF0A2B1E),
                specialKeyText = Color(0xFF34D399),
                accent = Color(0xFF059669),
                accentText = Color(0xFFFFFFFF),
                suggestionBackground = Color(0xFF0B3323),
                suggestionText = Color(0xFFF0FDF4),
                border = Color(0x4434D399)
            )
            KeyboardThemeType.CHERRY_BLOSSOM_WHITE -> KeyboardPalette(
                background = Color(0xFFFFF5F7),
                surface = Color(0xFFFFE4E8),
                keyBackground = Color(0xFFFFFFFF),
                keyText = Color(0xFFBE123C),
                keySecondaryText = Color(0xFFFB7185),
                specialKeyBackground = Color(0xFFFFE4E8),
                specialKeyText = Color(0xFF9F1239),
                accent = Color(0xFFE11D48),
                accentText = Color(0xFFFFFFFF),
                suggestionBackground = Color(0xFFFFFFFF),
                suggestionText = Color(0xFF9F1239),
                border = Color(0x33FB7185)
            )
            KeyboardThemeType.MATCHA_CREAM -> KeyboardPalette(
                background = Color(0xFFF4F7F4),
                surface = Color(0xFFE8EFE9),
                keyBackground = Color(0xFFFFFFFF),
                keyText = Color(0xFF14532D),
                keySecondaryText = Color(0xFF4ADE80),
                specialKeyBackground = Color(0xFFDCFCE7),
                specialKeyText = Color(0xFF15803D),
                accent = Color(0xFF16A34A),
                accentText = Color(0xFFFFFFFF),
                suggestionBackground = Color(0xFFFFFFFF),
                suggestionText = Color(0xFF14532D),
                border = Color(0x334ADE80)
            )
            KeyboardThemeType.COFFEE_ESPRESSO -> KeyboardPalette(
                background = Color(0xFFFAF6F0),
                surface = Color(0xFFEDE4D8),
                keyBackground = Color(0xFFFFFFFF),
                keyText = Color(0xFF451A03),
                keySecondaryText = Color(0xFFB45309),
                specialKeyBackground = Color(0xFFF5ECE0),
                specialKeyText = Color(0xFF92400E),
                accent = Color(0xFFD97706),
                accentText = Color(0xFFFFFFFF),
                suggestionBackground = Color(0xFFFFFFFF),
                suggestionText = Color(0xFF451A03),
                border = Color(0x33D97706)
            )
            KeyboardThemeType.CYBER_NEON_CONTRAST -> KeyboardPalette(
                background = Color(0xFF0A0A0F),
                surface = Color(0xFF161622),
                keyBackground = Color(0xFF1A1A28),
                keyText = Color(0xFFFDE047),
                keySecondaryText = Color(0xFFF472B6),
                specialKeyBackground = Color(0xFF221E36),
                specialKeyText = Color(0xFF38BDF8),
                accent = Color(0xFFF43F5E),
                accentText = Color(0xFFFFFFFF),
                suggestionBackground = Color(0xFF141420),
                suggestionText = Color(0xFFFDE047),
                border = Color(0x44F472B6)
            )
            KeyboardThemeType.NORDIC_GLACIER -> KeyboardPalette(
                background = Color(0xFF08121E),
                surface = Color(0xFF102030),
                keyBackground = Color(0xFF162A40),
                keyText = Color(0xFF38BDF8),
                keySecondaryText = Color(0xFF7DD3FC),
                specialKeyBackground = Color(0xFF0E1E30),
                specialKeyText = Color(0xFF67E8F9),
                accent = Color(0xFF0284C7),
                accentText = Color(0xFFFFFFFF),
                suggestionBackground = Color(0xFF102030),
                suggestionText = Color(0xFFE0F2FE),
                border = Color(0x3338BDF8)
            )
        }
    }
}

