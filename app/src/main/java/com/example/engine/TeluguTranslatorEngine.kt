package com.example.engine

object TeluguTranslatorEngine {

    private val ENG_TO_TELUGU_DICT = mapOf(
        "hello" to "నమస్కారం",
        "hi" to "హాయ్",
        "how are you" to "ఎలా ఉన్నారు?",
        "how are you?" to "ఎలా ఉన్నారు?",
        "i am fine" to "నేను బాగున్నాను",
        "what are you doing" to "ఏం చేస్తున్నారు?",
        "what are you doing?" to "ఏం చేస్తున్నారు?",
        "good morning" to "శుభోదయం",
        "good night" to "శుభరాత్రి",
        "good evening" to "శుభ సాయంత్రం",
        "good afternoon" to "శుభ మధ్యాహ్నం",
        "thank you" to "ధన్యవాదాలు",
        "thanks" to "కృతజ్ఞతలు",
        "welcome" to "స్వాగతం",
        "please" to "దయచేసి",
        "sorry" to "క్షమించండి",
        "congratulations" to "అభినందనలు",
        "happy birthday" to "జన్మదిన శుభాకాంక్షలు",
        "all the best" to "ఆల్ ది బెస్ట్ / శుభం కలగాలి",
        "where are you" to "ఎక్కడ ఉన్నారు?",
        "where are you?" to "ఎక్కడ ఉన్నారు?",
        "i am coming" to "నేను వస్తున్నాను",
        "call me" to "నాకు కాల్ చేయండి",
        "see you soon" to "త్వరలో కలుద్దాం",
        "see you later" to "మళ్ళీ కలుద్దాం",
        "ok" to "సరే",
        "yes" to "అవును",
        "no" to "కాదు",
        "what happened" to "ఏమైంది?",
        "what is your name" to "మీ పేరేంటి?",
        "my name is" to "నా పేరు",
        "i will call you back" to "నేను మీకు మళ్ళీ కాల్ చేస్తాను",
        "can you help me" to "నాకు సహాయం చేయగలరా?",
        "have a nice day" to "ఈ రోజు మీకు మంచిగా గడవాలి",
        "take care" to "జాగ్రత్తగా ఉండండి",
        "let's meet" to "కలుద్దాం",
        "i am busy" to "నేను బిజీగా ఉన్నాను",
        "i will reach in 5 minutes" to "నేను 5 నిమిషాల్లో చేరుకుంటాను",
        "nice to meet you" to "మిమ్మల్ని కలవడం చాలా సంతోషంగా ఉంది",
        "what is the time" to "సమయం ఎంత అయింది?"
    )

    private val TELUGU_TO_ENG_DICT = mapOf(
        "నమస్కారం" to "Hello / Greetings",
        "ఎలా ఉన్నారు" to "How are you?",
        "బాగున్నాను" to "I am fine",
        "ఏం చేస్తున్నారు" to "What are you doing?",
        "శుభోదయం" to "Good Morning",
        "శుభరాత్రి" to "Good Night",
        "ధన్యవాదాలు" to "Thank you",
        "కృతజ్ఞతలు" to "Thanks a lot",
        "క్షమించండి" to "Sorry / Excuse me",
        "అభినందనలు" to "Congratulations",
        "జన్మదిన శుభాకాంక్షలు" to "Happy Birthday",
        "ఎక్కడ ఉన్నారు" to "Where are you?",
        "వస్తున్నాను" to "I am coming",
        "కాల్ చేయండి" to "Please call me",
        "త్వరలో కలుద్దాం" to "See you soon",
        "సరే" to "Okay",
        "అవును" to "Yes",
        "కాదు" to "No",
        "ఏమైంది" to "What happened?",
        "మీ పేరేంటి" to "What is your name?",
        "జాగ్రత్తగా ఉండండి" to "Take care",
        "సహాయం చేయగలరా" to "Can you please help?"
    )

    fun translateEnglishToTelugu(input: String): String {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return ""

        val lower = trimmed.lowercase()
        // Exact match
        ENG_TO_TELUGU_DICT[lower]?.let { return it }

        // Partial match for common phrases
        for ((k, v) in ENG_TO_TELUGU_DICT) {
            if (lower == k || lower.startsWith("$k ") || lower.endsWith(" $k")) {
                val replaced = lower.replace(k, v)
                return replaced
            }
        }

        // If it's a single word or transliterated phrase, use transliteration engine
        val words = trimmed.split(Regex("\\s+"))
        val translatedWords = words.map { word ->
            val clean = word.lowercase().trim('.', ',', '?', '!')
            ENG_TO_TELUGU_DICT[clean] ?: TeluguTransliterationEngine.transliterate(clean).first
        }
        return translatedWords.joinToString(" ")
    }

    fun translateTeluguToEnglish(input: String): String {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return ""

        // Exact match
        TELUGU_TO_ENG_DICT[trimmed]?.let { return it }

        for ((k, v) in TELUGU_TO_ENG_DICT) {
            if (trimmed.contains(k)) {
                return trimmed.replace(k, v)
            }
        }

        return "Translation: $trimmed (Meaning: Telugu message)"
    }
}
