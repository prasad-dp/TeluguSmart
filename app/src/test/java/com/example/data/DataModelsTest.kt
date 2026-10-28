package com.example.data

import org.junit.Assert.*
import org.junit.Test

class DataModelsTest {

    @Test
    fun `test UserWord entity properties and defaults`() {
        val word = UserWord(
            teluguWord = "నమస్కారం",
            englishPhonetic = "namaskaram"
        )
        assertEquals(0L, word.id)
        assertEquals("నమస్కారం", word.teluguWord)
        assertEquals("namaskaram", word.englishPhonetic)
        assertEquals(1, word.frequency)
        assertTrue(word.lastUsedTimestamp > 0)
    }

    @Test
    fun `test ClipboardSnippet entity properties`() {
        val snippet = ClipboardSnippet(
            content = "123456",
            detectedType = "OTP",
            isOtpOrPhone = true
        )
        assertEquals("123456", snippet.content)
        assertEquals("OTP", snippet.detectedType)
        assertTrue(snippet.isOtpOrPhone)
        assertFalse(snippet.isPinned)
    }

    @Test
    fun `test KeyboardEffectType IDs are unique`() {
        val ids = KeyboardEffectType.values().map { it.id }
        assertEquals("All effect IDs must be unique", ids.size, ids.toSet().size)
        for (effect in KeyboardEffectType.values()) {
            assertTrue(effect.displayName.isNotEmpty())
            assertTrue(effect.description.isNotEmpty())
        }
    }

    @Test
    fun `test KeyboardSoundProfile IDs are unique`() {
        val ids = KeyboardSoundProfile.values().map { it.id }
        assertEquals("All sound profile IDs must be unique", ids.size, ids.toSet().size)
    }

    @Test
    fun `test HapticStrength duration ordering`() {
        assertTrue(HapticStrength.LIGHT.durationMs < HapticStrength.MEDIUM.durationMs)
        assertTrue(HapticStrength.MEDIUM.durationMs < HapticStrength.STRONG.durationMs)
    }

    @Test
    fun `test KeyboardPreferences default configuration`() {
        val prefs = KeyboardPreferences()
        assertEquals(KeyboardThemeType.MIDNIGHT_SLATE, prefs.themeType)
        assertTrue(prefs.keyHapticFeedback)
        assertFalse(prefs.keySoundFeedback)
        assertEquals(100, prefs.keyboardHeightPercent)
        assertTrue(prefs.showNumberRow)
        assertTrue(prefs.autoTransliterateOnSpace)
        assertEquals(OneHandedMode.OFF, prefs.oneHandedMode)
    }
}
