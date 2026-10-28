package com.example.engine

import org.junit.Assert.*
import org.junit.Test

class TeluguScriptConstantsTest {

    @Test
    fun `test vowels and consonants definitions`() {
        assertEquals(16, TeluguScriptConstants.VOWELS.size)
        assertTrue(TeluguScriptConstants.VOWELS.contains("అ"))
        assertTrue(TeluguScriptConstants.VOWELS.contains("ఆ"))
        assertTrue(TeluguScriptConstants.VOWELS.contains("అం"))
        assertTrue(TeluguScriptConstants.VOWELS.contains("అః"))

        assertTrue(TeluguScriptConstants.CONSONANTS.size >= 30)
        assertTrue(TeluguScriptConstants.CONSONANTS.contains("క"))
        assertTrue(TeluguScriptConstants.CONSONANTS.contains("ఱ"))
        assertTrue(TeluguScriptConstants.CONSONANTS.contains("క్ష"))
    }

    @Test
    fun `test matras and labels consistency`() {
        assertEquals(16, TeluguScriptConstants.MATRAS.size)
        assertEquals(16, TeluguScriptConstants.MATRA_LABELS.size)
        assertEquals("", TeluguScriptConstants.MATRAS[0]) // inherent a has no explicit matra
        assertEquals("ా", TeluguScriptConstants.MATRAS[1]) // aa dirgham
        assertEquals("్", TeluguScriptConstants.MATRAS[15]) // pollu / virama
    }

    @Test
    fun `test telugu digits mapping`() {
        assertEquals(10, TeluguScriptConstants.TELUGU_DIGITS.size)
        assertEquals("౦", TeluguScriptConstants.TELUGU_DIGITS[0])
        assertEquals("౧", TeluguScriptConstants.TELUGU_DIGITS[1])
        assertEquals("౫", TeluguScriptConstants.TELUGU_DIGITS[5])
        assertEquals("౯", TeluguScriptConstants.TELUGU_DIGITS[9])
    }

    @Test
    fun `test getGuninthaluFor produces 16 standard forms`() {
        val guninthalu = TeluguScriptConstants.getGuninthaluFor("క")
        assertEquals(16, guninthalu.size)
        assertEquals("క", guninthalu[0])
        assertEquals("కా", guninthalu[1])
        assertEquals("కి", guninthalu[2])
        assertEquals("కీ", guninthalu[3])
        assertEquals("కు", guninthalu[4])
        assertEquals("కూ", guninthalu[5])
        assertEquals("కృ", guninthalu[6])
        assertEquals("కె", guninthalu[7])
        assertEquals("కే", guninthalu[8])
        assertEquals("కై", guninthalu[9])
        assertEquals("కొ", guninthalu[10])
        assertEquals("కో", guninthalu[11])
        assertEquals("కౌ", guninthalu[12])
        assertEquals("కం", guninthalu[13])
        assertEquals("కః", guninthalu[14])
        assertEquals("క్", guninthalu[15])
    }

    @Test
    fun `test getGuninthaluFor strips virama if consonant has pollu`() {
        val fromPollu = TeluguScriptConstants.getGuninthaluFor("క్")
        val fromClean = TeluguScriptConstants.getGuninthaluFor("క")
        assertEquals(fromClean, fromPollu)
    }

    @Test
    fun `test getVottuluFor generates conjunct forms`() {
        val vottulu = TeluguScriptConstants.getVottuluFor("క")
        assertEquals(TeluguScriptConstants.COMMON_VOTTULU_CONSONANTS.size, vottulu.size)
        // First one is "క" + "్" + "క" = "క్క"
        assertEquals("క్క", vottulu[0])
        // With "గ" = "క్గ"
        assertEquals("క్గ", vottulu[1])
        // With "ర" = "క్ర"
        assertTrue(vottulu.contains("క్ర"))
        // With "య" = "క్య"
        assertTrue(vottulu.contains("క్య"))
    }
}
