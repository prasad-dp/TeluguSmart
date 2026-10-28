package com.example.engine

import org.junit.Assert.*
import org.junit.Test

class ToneRephraserTest {

    @Test
    fun `test empty and whitespace input returns empty string across all tones`() {
        for (tone in TeluguToneType.values()) {
            assertEquals("", ToneRephraser.rephraseText("", tone))
            assertEquals("", ToneRephraser.rephraseText("    ", tone))
        }
    }

    @Test
    fun `test all TeluguToneType enum properties are populated`() {
        for (tone in TeluguToneType.values()) {
            assertTrue(tone.label.isNotEmpty())
            assertTrue(tone.teluguLabel.isNotEmpty())
            assertTrue(tone.iconName.isNotEmpty())
        }
    }

    @Test
    fun `test casual tone transformation`() {
        val result = ToneRephraser.rephraseText("మీరు రండి, చెప్పండి", TeluguToneType.CASUAL)
        assertTrue(result.contains("నువ్వు"))
        assertTrue(result.contains("రా"))
        assertTrue(result.contains("చెప్పు"))
        assertTrue(result.contains("బ్రో") || result.contains("మావా") || result.contains("రా"))
    }

    @Test
    fun `test formal tone transformation`() {
        val result = ToneRephraser.rephraseText("నువ్వు రా, చెప్పు", TeluguToneType.FORMAL)
        assertTrue(result.contains("మీరు"))
        assertTrue(result.contains("రండి"))
        assertTrue(result.contains("చెప్పండి"))
        assertTrue(result.contains("నమస్కారం అండి"))
    }

    @Test
    fun `test poetic tone transformation`() {
        val result = ToneRephraser.rephraseText("నమస్కారం, సినిమా బాగుంది", TeluguToneType.POETIC)
        assertTrue(result.contains("వందనములు"))
        assertTrue(result.contains("చిత్రరాజము"))
        assertTrue(result.contains("భారతీయ సంస్కృతితో కూడిన మధుర వాక్యం"))
    }

    @Test
    fun `test english translation tone`() {
        val greeting = ToneRephraser.rephraseText("నమస్కారం", TeluguToneType.ENGLISH_TRANSLATE)
        assertEquals("Hello and Greetings!", greeting)

        val thanks = ToneRephraser.rephraseText("ధన్యవాదాలు", TeluguToneType.ENGLISH_TRANSLATE)
        assertEquals("Thank you very much!", thanks)

        val fallback = ToneRephraser.rephraseText("కొత్త మాట", TeluguToneType.ENGLISH_TRANSLATE)
        assertEquals("Translation: \"కొత్త మాట\"", fallback)
    }

    @Test
    fun `test summary tone`() {
        val shortText = ToneRephraser.rephraseText("మనం రేపు కలుద్దాం", TeluguToneType.SUMMARY)
        assertEquals("ముఖ్యాంశం: మనం రేపు కలుద్దాం", shortText)

        val longText = ToneRephraser.rephraseText("ఈ రోజు ఉదయం మనం అందరం ఒకచోట చేరి ముఖ్యమైన విషయాలు చర్చించాము", TeluguToneType.SUMMARY)
        assertTrue(longText.startsWith("ముఖ్యాంశం: "))
        assertTrue(longText.endsWith("..."))
    }
}
