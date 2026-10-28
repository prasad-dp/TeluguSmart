package com.example.engine

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TeluguTransliterationEngineTest {

    @Test
    fun `test exact dictionary match for common greetings`() {
        val (telugu, raw, _) = TeluguTransliterationEngine.transliterate("namaste")
        assertEquals("నమస్తే", telugu)
        assertEquals("namaste", raw)

        val (namaskaram, _, _) = TeluguTransliterationEngine.transliterate("namaskaram")
        assertEquals("నమస్కారం", namaskaram)

        val (dhanya, _, _) = TeluguTransliterationEngine.transliterate("dhanyavadalu")
        assertEquals("ధన్యవాదాలు", dhanya)
    }

    @Test
    fun `test dictionary case-insensitivity`() {
        val (upper, _, _) = TeluguTransliterationEngine.transliterate("NAMASTE")
        assertEquals("నమస్తే", upper)

        val (mixed, _, _) = TeluguTransliterationEngine.transliterate("BaGuNnAvA")
        assertEquals("బాగున్నావా", mixed)
    }

    @Test
    fun `test conversational and festival word expansions`() {
        val (ugadi, _, _) = TeluguTransliterationEngine.transliterate("ugadi")
        assertEquals("ఉగాది", ugadi)

        val (sankranti, _, _) = TeluguTransliterationEngine.transliterate("sankranti")
        assertEquals("సంక్రాంతి", sankranti)

        val (bagundi, _, _) = TeluguTransliterationEngine.transliterate("bagundi")
        assertEquals("బాగుంది", bagundi)

        val (cinema, _, _) = TeluguTransliterationEngine.transliterate("cinema")
        assertEquals("సినిమా", cinema)
    }

    @Test
    fun `test empty and whitespace input handling`() {
        val emptyResult = TeluguTransliterationEngine.transliterate("")
        assertTrue(emptyResult.first.isNotEmpty())
        assertTrue(emptyResult.second.isEmpty())
        assertTrue(emptyResult.third.isNotEmpty())

        val spacesResult = TeluguTransliterationEngine.transliterate("     ")
        assertTrue(spacesResult.first.isNotEmpty())

        // Leading and trailing spaces should be trimmed before lookup
        val trimmedMatch = TeluguTransliterationEngine.transliterate("  repu  ")
        assertEquals("రేపు", trimmedMatch.first)
    }

    @Test
    fun `test contextual next word predictions on empty input`() {
        val resultWithContext = TeluguTransliterationEngine.transliterate("", previousWord = "ఉగాది")
        assertEquals("శుభాకాంక్షలు", resultWithContext.first)

        val resultWithUnknownContext = TeluguTransliterationEngine.transliterate("", previousWord = "unknown_context_xyz")
        assertTrue(resultWithContext.first.isNotEmpty())
        assertTrue(resultWithUnknownContext.first.isNotEmpty())
    }

    @Test
    fun `test phonetic converter with independent vowels`() {
        assertEquals("అ", TeluguTransliterationEngine.convertPhonetic("a"))
        assertEquals("ఆ", TeluguTransliterationEngine.convertPhonetic("aa"))
        assertEquals("ఇ", TeluguTransliterationEngine.convertPhonetic("i"))
        assertEquals("ఈ", TeluguTransliterationEngine.convertPhonetic("ii"))
        assertEquals("ఉ", TeluguTransliterationEngine.convertPhonetic("u"))
        assertEquals("ఊ", TeluguTransliterationEngine.convertPhonetic("uu"))
        assertEquals("ఎ", TeluguTransliterationEngine.convertPhonetic("e"))
        assertEquals("ఏ", TeluguTransliterationEngine.convertPhonetic("ea"))
        assertEquals("ఐ", TeluguTransliterationEngine.convertPhonetic("ai"))
        assertEquals("ఒ", TeluguTransliterationEngine.convertPhonetic("o"))
        assertEquals("ఓ", TeluguTransliterationEngine.convertPhonetic("oa"))
        assertEquals("ఔ", TeluguTransliterationEngine.convertPhonetic("au"))
        assertEquals("అం", TeluguTransliterationEngine.convertPhonetic("am"))
    }

    @Test
    fun `test phonetic converter with consonants and matras`() {
        assertEquals("క", TeluguTransliterationEngine.convertPhonetic("ka"))
        assertEquals("కా", TeluguTransliterationEngine.convertPhonetic("kaa"))
        assertEquals("కి", TeluguTransliterationEngine.convertPhonetic("ki"))
        assertEquals("కీ", TeluguTransliterationEngine.convertPhonetic("kee"))
        assertEquals("కు", TeluguTransliterationEngine.convertPhonetic("ku"))
        assertEquals("కూ", TeluguTransliterationEngine.convertPhonetic("koo"))
        assertEquals("కె", TeluguTransliterationEngine.convertPhonetic("ke"))
        assertEquals("కే", TeluguTransliterationEngine.convertPhonetic("kea"))
        assertEquals("కై", TeluguTransliterationEngine.convertPhonetic("kai"))
        assertEquals("కొ", TeluguTransliterationEngine.convertPhonetic("ko"))
        assertEquals("కో", TeluguTransliterationEngine.convertPhonetic("koa"))
        assertEquals("కౌ", TeluguTransliterationEngine.convertPhonetic("kau"))
        assertEquals("కం", TeluguTransliterationEngine.convertPhonetic("kam"))
    }

    @Test
    fun `test compound consonants and conjuncts`() {
        assertEquals("క్క", TeluguTransliterationEngine.convertPhonetic("kka"))
        assertEquals("త్త", TeluguTransliterationEngine.convertPhonetic("tta"))
        assertEquals("ప్ప", TeluguTransliterationEngine.convertPhonetic("ppa"))
        assertEquals("మ్మ", TeluguTransliterationEngine.convertPhonetic("mma"))
        assertEquals("య్య", TeluguTransliterationEngine.convertPhonetic("yya"))
        assertEquals("ల్ల", TeluguTransliterationEngine.convertPhonetic("lla"))
        assertEquals("స్త్ర", TeluguTransliterationEngine.convertPhonetic("stra"))
        assertEquals("క్ష", TeluguTransliterationEngine.convertPhonetic("ksha"))
        assertEquals("ప్ర", TeluguTransliterationEngine.convertPhonetic("pra"))
        assertEquals("త్ర", TeluguTransliterationEngine.convertPhonetic("tra"))
    }

    @Test
    fun `test digits and punctuation preservation`() {
        // Standard ASCII digits to Telugu digits
        assertEquals("౧౨౩౪౫", TeluguTransliterationEngine.convertPhonetic("12345"))
        assertEquals("౬౭౮౯౦", TeluguTransliterationEngine.convertPhonetic("67890"))

        // Punctuation should be preserved
        val withPunctuation = TeluguTransliterationEngine.convertPhonetic("123?!")
        assertEquals("౧౨౩?!", withPunctuation)
    }

    @Test
    fun `test getNextWords returns relevant list or default fallback`() {
        val ugadiNext = TeluguTransliterationEngine.getNextWords("ఉగాది")
        assertTrue(ugadiNext.contains("శుభాకాంక్షలు"))

        val namasteNext = TeluguTransliterationEngine.getNextWords("నమస్తే")
        assertTrue(namasteNext.isNotEmpty())

        val unknownNext = TeluguTransliterationEngine.getNextWords("కొత్తపదం")
        assertEquals(listOf("అండి", "మిత్రమా", "సరే", "చూద్దాం"), unknownNext)
    }

    @Test
    fun `test searchDictionary queries`() {
        // Empty query should return first default batch
        val emptyQuery = TeluguTransliterationEngine.searchDictionary("")
        assertEquals(40, emptyQuery.size)

        // Matching English query
        val namasteSearch = TeluguTransliterationEngine.searchDictionary("namaste")
        assertTrue(namasteSearch.any { it.first == "namaste" && it.second == "నమస్తే" })

        // Matching Telugu query
        val teluguSearch = TeluguTransliterationEngine.searchDictionary("నమస్తే")
        assertTrue(teluguSearch.any { it.first == "namaste" && it.second == "నమస్తే" })

        // Case-insensitive query
        val upperSearch = TeluguTransliterationEngine.searchDictionary("UGADI")
        assertTrue(upperSearch.any { it.first == "ugadi" })

        // Non-existent word
        val notFound = TeluguTransliterationEngine.searchDictionary("non_existent_gibberish_12345")
        assertTrue(notFound.isEmpty())
    }

    @Test
    fun `test LRU cache repeated transliteration consistency`() {
        val word = "kaluddam"
        val firstRun = TeluguTransliterationEngine.transliterate(word)
        val secondRun = TeluguTransliterationEngine.transliterate(word)
        val thirdRun = TeluguTransliterationEngine.transliterate(word)

        assertEquals(firstRun, secondRun)
        assertEquals(secondRun, thirdRun)
        assertEquals("కలుద్దాం", firstRun.first)
    }
}

