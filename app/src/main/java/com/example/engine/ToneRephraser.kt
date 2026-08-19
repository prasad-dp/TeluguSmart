package com.example.engine

enum class TeluguToneType(val label: String, val teluguLabel: String, val iconName: String) {
    CASUAL("Casual / Friendly", "స్నేహపూర్వక శైలి (Friendly)", "mood"),
    FORMAL("Formal / Respectful", "గౌరవప్రదమైన శైలి (Formal)", "business"),
    POETIC("Classical / Pure Telugu", "స్వచ్ఛమైన గ్రాంథిక శైలి (Poetic)", "auto_stories"),
    ENGLISH_TRANSLATE("Translate to English", "ఆంగ్లంలోకి అనువాదం (English)", "translate"),
    SUMMARY("Quick Summary", "సంక్షిప్త సారాంశం (Summary)", "compress")
}

object ToneRephraser {

    fun rephraseText(input: String, tone: TeluguToneType): String {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return ""

        return when (tone) {
            TeluguToneType.CASUAL -> toCasual(trimmed)
            TeluguToneType.FORMAL -> toFormal(trimmed)
            TeluguToneType.POETIC -> toPoetic(trimmed)
            TeluguToneType.ENGLISH_TRANSLATE -> toEnglishTranslation(trimmed)
            TeluguToneType.SUMMARY -> toSummary(trimmed)
        }
    }

    private fun toCasual(text: String): String {
        var res = text
        val replacements = mapOf(
            "రండి" to "రా",
            "చేయండి" to "చెయ్",
            "చెప్పండి" to "చెప్పు",
            "నమస్కారం" to "హాయ్ మచ్చా",
            "బాగున్నారా" to "బాగున్నావా బ్రో",
            "తిన్నారా" to "తిన్నావా రా",
            "వస్తారా" to "వస్తావా",
            "ఎలా ఉన్నారు" to "ఎలా ఉన్నావ్ మావా",
            "ధన్యవాదాలు" to "థాంక్స్ రా",
            "మీరు" to "నువ్వు"
        )
        replacements.forEach { (k, v) ->
            res = res.replace(k, v)
        }
        if (!res.contains("బ్రో") && !res.contains("మావా") && !res.contains("రా")) {
            res = "$res (బ్రో / మావా)"
        }
        return res
    }

    private fun toFormal(text: String): String {
        var res = text
        val replacements = mapOf(
            "రా" to "రండి",
            "చెయ్" to "చేయండి",
            "చెప్పు" to "చెప్పండి",
            "బాగున్నావా" to "బాగున్నారా",
            "తిన్నావా" to "తిన్నారా",
            "వస్తావా" to "వస్తారా",
            "నువ్వు" to "మీరు",
            "ఎలా ఉన్నావ్" to "ఎలా ఉన్నారు",
            "థాంక్స్" to "హృదయపూర్వక ధన్యవాదాలు"
        )
        replacements.forEach { (k, v) ->
            res = res.replace(k, v)
        }
        if (!res.endsWith("అండి") && !res.endsWith("సార్")) {
            res = "$res, నమస్కారం అండి."
        }
        return res
    }

    private fun toPoetic(text: String): String {
        var res = text
        val replacements = mapOf(
            "బాగున్నావా" to "కుశలమా మిత్రమా",
            "నమస్కారం" to "వందనములు",
            "మంచి" to "ఉత్తమోత్తమ",
            "స్నేహితుడు" to "ఆప్తమిత్రుడు",
            "సంతోషం" to "ఆనంద పరవశం",
            "చాలా" to "అత్యద్భుతముగా",
            "సినిమా" to "చిత్రరాజము"
        )
        replacements.forEach { (k, v) ->
            res = res.replace(k, v)
        }
        return "భారతీయ సంస్కృతితో కూడిన మధుర వాక్యం: $res"
    }

    private fun toEnglishTranslation(text: String): String {
        val lower = text.lowercase()
        return when {
            lower.contains("నమస్కారం") || lower.contains("నమస్తే") -> "Hello and Greetings!"
            lower.contains("బాగున్నావా") -> "How are you doing, friend?"
            lower.contains("బాగున్నారా") -> "How are you doing, sir/madam?"
            lower.contains("రేపు కలుద్దాం") -> "Let's meet tomorrow!"
            lower.contains("ధన్యవాదాలు") -> "Thank you very much!"
            lower.contains("శుభోదయం") -> "Good Morning!"
            lower.contains("శుభరాత్రి") -> "Good Night!"
            lower.contains("తిన్నావా") -> "Have you eaten?"
            lower.contains("ఏం చేస్తున్నావ్") -> "What are you doing?"
            lower.contains("సినిమా") -> "The movie / cinema"
            else -> "Translation: \"$text\""
        }
    }

    private fun toSummary(text: String): String {
        val words = text.split(" ")
        return if (words.size > 5) {
            "ముఖ్యాంశం: " + words.take(4).joinToString(" ") + "..."
        } else {
            "ముఖ్యాంశం: $text"
        }
    }
}
