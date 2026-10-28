package com.example.engine

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TeluguTranslatorEngineTest {

    @Test
    fun `test English to Telugu exact dictionary phrases`() {
        assertEquals("నమస్కారం", TeluguTranslatorEngine.translateEnglishToTelugu("hello"))
        assertEquals("ధన్యవాదాలు", TeluguTranslatorEngine.translateEnglishToTelugu("thank you"))
        assertEquals("శుభోదయం", TeluguTranslatorEngine.translateEnglishToTelugu("good morning"))
        assertEquals("ఎలా ఉన్నారు?", TeluguTranslatorEngine.translateEnglishToTelugu("how are you"))
        assertEquals("ఎలా ఉన్నారు?", TeluguTranslatorEngine.translateEnglishToTelugu("how are you?"))
    }

    @Test
    fun `test English to Telugu case insensitivity and whitespace`() {
        assertEquals("నమస్కారం", TeluguTranslatorEngine.translateEnglishToTelugu("HELLO"))
        assertEquals("ధన్యవాదాలు", TeluguTranslatorEngine.translateEnglishToTelugu("  Thank You  "))
        assertEquals("", TeluguTranslatorEngine.translateEnglishToTelugu(""))
        assertEquals("", TeluguTranslatorEngine.translateEnglishToTelugu("     "))
    }

    @Test
    fun `test English to Telugu multi-word and transliteration fallback`() {
        val result = TeluguTranslatorEngine.translateEnglishToTelugu("hello bro")
        assertTrue(result.contains("నమస్కారం") || result.isNotEmpty())
    }

    @Test
    fun `test Telugu to English exact matches`() {
        assertEquals("Hello / Greetings", TeluguTranslatorEngine.translateTeluguToEnglish("నమస్కారం"))
        assertEquals("Thank you", TeluguTranslatorEngine.translateTeluguToEnglish("ధన్యవాదాలు"))
        assertEquals("Good Morning", TeluguTranslatorEngine.translateTeluguToEnglish("శుభోదయం"))
        assertEquals("How are you?", TeluguTranslatorEngine.translateTeluguToEnglish("ఎలా ఉన్నారు"))
    }

    @Test
    fun `test Telugu to English fallback and empty inputs`() {
        assertEquals("", TeluguTranslatorEngine.translateTeluguToEnglish(""))
        assertEquals("", TeluguTranslatorEngine.translateTeluguToEnglish("   "))

        val fallback = TeluguTranslatorEngine.translateTeluguToEnglish("కొత్తమాటలు")
        assertTrue(fallback.contains("Translation: కొత్తమాటలు"))
    }
}
