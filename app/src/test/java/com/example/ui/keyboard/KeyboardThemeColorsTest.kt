package com.example.ui.keyboard

import androidx.compose.ui.graphics.Color
import com.example.data.KeyboardThemeType
import org.junit.Assert.*
import org.junit.Test

class KeyboardThemeColorsTest {

    @Test
    fun `test all KeyboardThemeType values return valid non-null palettes`() {
        for (theme in KeyboardThemeType.values()) {
            val palette = KeyboardThemeColors.getPalette(theme)
            assertNotNull("Palette for $theme should not be null", palette)
            assertNotEquals(Color.Unspecified, palette.background)
            assertNotEquals(Color.Unspecified, palette.surface)
            assertNotEquals(Color.Unspecified, palette.keyBackground)
            assertNotEquals(Color.Unspecified, palette.keyText)
            assertNotEquals(Color.Unspecified, palette.accent)
        }
    }

    @Test
    fun `test customAccentHex properly overrides accent and specialKeyText`() {
        val customHex = 0xFFFF0055L // Vivid pink
        val expectedColor = Color(customHex)

        val palette = KeyboardThemeColors.getPalette(KeyboardThemeType.PITCH_BLACK, customHex)
        assertEquals(expectedColor, palette.accent)
        assertEquals(expectedColor, palette.specialKeyText)
    }

    @Test
    fun `test without customAccentHex preserves base theme accent`() {
        val palette = KeyboardThemeColors.getPalette(KeyboardThemeType.PITCH_BLACK, null)
        assertEquals(Color(0xFF0284C7), palette.accent)
    }
}
