package com.example.engine

object TeluguScriptConstants {
    // Vowels (అచ్చులు)
    val VOWELS = listOf(
        "అ", "ఆ", "ఇ", "ఈ", "ఉ", "ఊ", "ఋ", "ౠ", "ఎ", "ఏ", "ఐ", "ఒ", "ఓ", "ఔ", "అం", "అః"
    )

    // Consonants (హల్లులు)
    val CONSONANTS = listOf(
        "క", "ఖ", "గ", "ఘ", "ఙ",
        "చ", "ఛ", "జ", "ఝ", "ఞ",
        "ట", "ఠ", "డ", "ఢ", "ణ",
        "త", "థ", "ద", "ధ", "న",
        "ప", "ఫ", "బ", "భ", "మ",
        "య", "ర", "ల", "వ", "శ",
        "ష", "స", "హ", "ళ", "క్ష", "ఱ"
    )

    // Matras (గుణింతాల గుర్తులు)
    val MATRAS = listOf(
        "", "ా", "ి", "ీ", "ు", "ూ", "ృ", "ె", "ే", "ై", "ొ", "ో", "ౌ", "ం", "ః", "్"
    )

    // Matra names / phonetic labels for popup
    val MATRA_LABELS = listOf(
        "అ (తలకట్టు)", "ఆ (దీర్ఘం)", "ఇ (గుడి)", "ఈ (గుడిదీర్ఘం)",
        "ఉ (కొమ్ము)", "ఊ (కొమ్ముదీర్ఘం)", "ఋ (వట్రుసుడి)", "ఎ (ఎత్వం)",
        "ఏ (ఏత్వం)", "ఐ (ఐత్వం)", "ఒ (ఒత్వం)", "ఓ (ఓత్వం)",
        "ఔ (ఔత్వం)", "అం (సున్న)", "అః (విసర్గ)", "్ (పొల్లు)"
    )

    // Telugu Digits
    val TELUGU_DIGITS = listOf("౦", "౧", "౨", "౩", "౪", "౫", "౬", "౭", "౮", "౯")

    // Common Vottulu (వొత్తులు)
    val COMMON_VOTTULU_CONSONANTS = listOf(
        "క", "గ", "చ", "జ", "ట", "డ", "ణ", "త", "ద", "న", "ప", "బ", "మ", "య", "ర", "ల", "వ", "శ", "ష", "స", "హ", "ళ", "క్ష"
    )

    /**
     * Generates the 16 standard Guninthalu forms for any given Telugu base consonant.
     */
    fun getGuninthaluFor(consonant: String): List<String> {
        val base = consonant.replace("్", "")
        return listOf(
            base,               // క
            base + "ా",          // కా
            base + "ి",          // కి
            base + "ీ",          // కీ
            base + "ు",          // కు
            base + "ూ",          // కూ
            base + "ృ",          // కృ
            base + "ె",          // కె
            base + "ే",          // కే
            base + "ై",          // కై
            base + "ొ",          // కొ
            base + "ో",          // కో
            base + "ౌ",          // కౌ
            base + "ం",          // కం
            base + "ః",          // కః
            base + "్"           // క్
        )
    }

    /**
     * Generates common conjunct vottulu applied to a base consonant.
     */
    fun getVottuluFor(consonant: String): List<String> {
        val base = consonant.replace("్", "")
        return COMMON_VOTTULU_CONSONANTS.map { vottuConsonant ->
            base + "్" + vottuConsonant
        }
    }
}
