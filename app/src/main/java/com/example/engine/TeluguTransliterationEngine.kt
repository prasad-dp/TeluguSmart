package com.example.engine

import android.util.LruCache

/**
 * Fast, zero-latency, offline Tenglish-to-Telugu phonetic transliteration engine.
 * Combines dictionary lookups with a rule-based finite state transducer (FST)
 * and an LRU cache for O(1) repeated query resolution.
 */
object TeluguTransliterationEngine {

    // High performance LRU Cache for phonetic translations (capacity: 256 entries)
    private val TRANSLITERATION_LRU_CACHE = LruCache<String, Triple<String, String, String>>(256)

    // Preloaded high-frequency Tenglish-to-Telugu dictionary
    private val DICTIONARY = mapOf(
        // Greetings & Civility
        "namaste" to "నమస్తే",
        "namaskaram" to "నమస్కారం",
        "namaskaralu" to "నమస్కారాలు",
        "dhanyavadalu" to "ధన్యవాదాలు",
        "shubhodhayam" to "శుభోదయం",
        "shubharatri" to "శుభరాత్రి",
        "swagatham" to "స్వాగతం",
        "shubhakankshalu" to "శుభాకాంక్షలు",
        "dayachesi" to "దయచేసి",
        "kshaminchandi" to "క్షమించండి",

        // Conversational & Common Inquiries
        "bagunnava" to "బాగున్నావా",
        "bagunnara" to "బాగున్నారా",
        "bagunnanu" to "బాగున్నాను",
        "enti" to "ఏంటి",
        "ente" to "ఏంటే",
        "ela" to "ఎలా",
        "ekkada" to "ఎక్కడ",
        "eppudu" to "ఎప్పుడు",
        "enduku" to "ఎందుకు",
        "evadu" to "ఎవడు",
        "evaru" to "ఎవరు",
        "emiti" to "ఏమిటి",
        "em chestunnav" to "ఏం చేస్తున్నావ్",
        "emchestunnav" to "ఏంచేస్తున్నావ్",
        "em chestunnaru" to "ఏం చేస్తున్నారు",
        "emchestunnaru" to "ఏంచేస్తున్నారు",
        "tinnava" to "తిన్నావా",
        "tinnara" to "తిన్నారా",
        "tinnanu" to "తిన్నాను",
        "bhojanam" to "భోజనం",
        "tiffin" to "టిఫిన్",
        "coffee" to "కాఫీ",
        "tea" to "టీ",
        "water" to "నీళ్లు",

        // Actions, Time, Meetings
        "repu" to "రేపు",
        "ivala" to "ఇవాళ",
        "eeroju" to "ఈరోజు",
        "ninna" to "నిన్న",
        "monna" to "మొన్న",
        "ippude" to "ఇప్పుడే",
        "ippudu" to "ఇప్పుడు",
        "appudu" to "అప్పుడు",
        "tarvata" to "తర్వాత",
        "mellaga" to "మెల్లగా",
        "veganga" to "వేగంగా",
        "kaludam" to "కలుద్దాం",
        "kaluddam" to "కలుద్దాం",
        "matladam" to "మాట్లాడదాం",
        "matladu" to "మాట్లాడు",
        "matladandi" to "మాట్లాడండి",
        "cheppu" to "చెప్పు",
        "cheppandi" to "చెప్పండి",
        "chesava" to "చేశావా",
        "chesara" to "చేశారా",
        "chesanu" to "చేశాను",
        "vastava" to "వస్తావా",
        "vastara" to "వస్తారా",
        "vastanu" to "వస్తాను",
        "veldam" to "వెళ్దాం",
        "velthunna" to "వెళ్తున్నా",
        "chusava" to "చూశావా",
        "chusara" to "చూశారా",
        "chusanu" to "చూశాను",
        "chudu" to "చూడు",
        "chudandi" to "చూడండి",
        "vinava" to "విన్నావా",
        "vinu" to "విను",
        "vinandi" to "వినండి",

        // Pop culture, Slang & Media
        "movie" to "సినిమా",
        "cinema" to "సినిమా",
        "ticket" to "టికెట్",
        "book" to "బుక్",
        "show" to "షో",
        "theatre" to "థియేటర్",
        "hero" to "హీరో",
        "heroine" to "హీరోయిన్",
        "director" to "డైరెక్టర్",
        "song" to "పాట",
        "paata" to "పాట",
        "paatalu" to "పాటలు",
        "super" to "సూపర్",
        "keka" to "కేక",
        "adbhutam" to "అద్భుతం",
        "adiripoyindi" to "అదిరిపోయింది",
        "arachakam" to "అరాచకం",
        "babu" to "బాబు",
        "mowa" to "మావా",
        "mama" to "మామ",
        "bro" to "బ్రో",
        "thammudu" to "తమ్ముడు",
        "annayya" to "అన్నయ్య",
        "akka" to "అక్క",
        "chelli" to "చెల్లి",
        "amma" to "అమ్మ",
        "nanna" to "నాన్న",
        "mitrama" to "మిత్రమా",
        "snehithuda" to "స్నేహితుడా",
        "pillalu" to "పిల్లలు",

        // Work, Places, Identity
        "telugu" to "తెలుగు",
        "telugodu" to "తెలుగోడు",
        "bhasha" to "భాష",
        "andhra" to "ఆంధ్ర",
        "telangana" to "తెలంగాణ",
        "hyderabad" to "హైదరాబాద్",
        "vizag" to "వైజాగ్",
        "vijayawada" to "విజయవాడ",
        "india" to "భారతదేశం",
        "office" to "ఆఫీస్",
        "work" to "పని",
        "pani" to "పని",
        "urgent" to "అర్జెంట్",
        "call" to "కాల్",
        "message" to "మెసేజ్",
        "number" to "నంబర్",
        "money" to "డబ్బులు",
        "dabbu" to "డబ్బు",
        "time" to "సమయం",
        "samayam" to "సమయం",
        "sneham" to "స్నేహం",
        "prema" to "ప్రేమ",
        "santhosham" to "సంతోషం",
        "anandam" to "ఆనందం",
        "nijam" to "నిజం",
        "abaddham" to "అబద్ధం",
        "chala" to "చాలా",
        "chaala" to "చాలా",
        "konchem" to "కొంచెం",
        "koncham" to "కొంచెం",
        "avunu" to "అవును",
        "avuna" to "అవునా",
        "kaadu" to "కాదు",
        "kadu" to "కాదు",
        "ledu" to "లేదు",
        "undi" to "ఉంది",
        "unnayi" to "ఉన్నాయి",
        "undandi" to "ఉండండి",
        "undu" to "ఉండు",
        "unnanu" to "ఉన్నాను",
        "unnamu" to "ఉన్నాము",
        "unnaru" to "ఉన్నారు",
        "unnav" to "ఉన్నావ్",
        "nenu" to "నేను",
        "nuvvu" to "నువ్వు",
        "meeru" to "మీరు",
        "manamu" to "మనము",
        "manaki" to "మనకి",
        "naaku" to "నాకు",
        "neeku" to "నీకు",
        "meeku" to "మీకు",
        "vaallu" to "వాళ్లు",
        "vallu" to "వాళ్ళు",
        "athanu" to "అతను",
        "aame" to "ఆమె",
        "idi" to "ఇది",
        "adi" to "అది",
        "ivi" to "ఇవి",
        "avi" to "అవి",
        "ikkada" to "ఇక్కడ",
        "akkada" to "అక్కడ",
        "andaru" to "అందరూ",
        "andariki" to "అందరికీ",
        "sare" to "సరే",
        "alage" to "అలాగే",
        "alane" to "అలానే",
        "manchi" to "మంచి",
        "chedda" to "చెడ్డ",
        "pedda" to "పెద్ద",
        "chinna" to "చిన్న",
        "kothadi" to "కొత్తది",
        "paathadi" to "పాతది",
        "telusa" to "తెలుసా",
        "telusu" to "తెలుసు",
        "teliyadu" to "తెలియదు",
        "chey" to "చేయ్",
        "cheyyi" to "చెయ్యి",
        "cheyandi" to "చేయండి",
        "cheddam" to "చేద్దాం",
        "kurcho" to "కూర్చో",
        "kurchondi" to "కూర్చోండి",
        "levu" to "లేవు",
        "ledu" to "లేదు"
    )

    // Contextual Next-Word Predictions (Bi-grams & Multi-word context)
    private val NEXT_WORD_PREDICTIONS = mapOf(
        "రేపు" to listOf("కలుద్దాం", "వస్తాను", "సినిమాకి", "మాట్లాడుకుందాం", "ఉదయం"),
        "కలుద్దాం" to listOf("మిత్రమా", "తప్పకుండా", "అక్కడ", "రేపు", "సాయంత్రం"),
        "బాగున్నావా" to listOf("మిత్రమా", "ఎలా", "చాలా", "రోజులైంది", "ఇంటిదగ్గర"),
        "బాగున్నారా" to listOf("అండి", "సార్", "అందరూ", "కులాసాయేనా", "ఎలా"),
        "నమస్కారం" to listOf("అండి", "సార్", "అందరికీ", "మిత్రులారా", "గురువుగారు"),
        "ధన్యవాదాలు" to listOf("మిత్రమా", "అండి", "సార్", "చాలా", "సహాయం"),
        "శుభోదయం" to listOf("అందరికీ", "మిత్రమా", "మీ", "రోజు", "బాగుండాలి"),
        "శుభాకాంక్షలు" to listOf("మిత్రమా", "అందరికీ", "మీకు", "కుటుంబానికి", "హృదయపూర్వక"),
        "పుట్టినరోజు" to listOf("శుభాకాంక్షలు", "వేడుకలు", "పార్టీ", "సందర్భంగా", "ప్రత్యేక"),
        "స్వాగతం" to listOf("సుస్వాగతం", "మిత్రమా", "అందరికీ", "వేదికపైకి", "నమస్కారం"),
        "క్షమించండి" to listOf("తప్పు", "ఆలస్యమైంది", "దయచేసి", "ఇంకోసారి", "సార్"),
        "ఎలా" to listOf("ఉన్నారు", "ఉన్నావు", "ఉంది", "జరిగింది", "చేయాలి"),
        "ఎక్కడ" to listOf("ఉన్నావు", "ఉన్నారు", "కలుద్దాం", "వెళ్తున్నావ్", "దొరుకుతుంది"),
        "ఎప్పుడు" to listOf("వస్తావు", "వస్తారు", "కలుద్దాం", "స్టార్ట్", "వెళ్దాం"),
        "ఎందుకు" to listOf("అలా", "ఇలా", "చేస్తున్నావ్", "రావట్లేదు", "కోపం"),
        "ఏంటి" to listOf("విశేషాలు", "సంగతులు", "సమాచారం", "చేస్తున్నావ్", "ప్లాన్స్"),
        "ఏం" to listOf("చేస్తున్నావ్", "చేస్తున్నారు", "తిన్నావు", "కావాలి", "సంగతి"),
        "తిన్నావా" to listOf("రా", "మిత్రమా", "ఏం", "ఇంకా", "లేదా"),
        "తిన్నారా" to listOf("అండి", "సార్", "ఏం", "భోజనం", "అందరూ"),
        "సినిమా" to listOf("చూశావా", "బాగుంది", "టికెట్లు", "సూపర్", "రిలీజ్"),
        "చాలా" to listOf("బాగుంది", "సంతోషం", "ధన్యవాదాలు", "కష్టం", "ముఖ్యమైన"),
        "తెలుగు" to listOf("భాష", "సాహిత్యం", "సంస్కృతి", "జాతి", "సినిమా"),
        "నేను" to listOf("వస్తున్నాను", "బాగున్నాను", "చేస్తాను", "వెళ్తున్నాను", "చెప్పాను"),
        "నువ్వు" to listOf("ఎలా ఉన్నావు", "ఎక్కడున్నావ్", "రా", "వస్తావా", "విను"),
        "మీరు" to listOf("ఎలా ఉన్నారు", "ఏం చేస్తున్నారు", "రండి", "చెప్పండి", "దయచేసి"),
        "ఆఫీస్" to listOf("కి వెళ్తున్నా", "పని ఉంది", "టైం అయింది", "నుంచి వచ్చా", "మీటింగ్"),
        "ఇంటికి" to listOf("వెళ్తున్నా", "వచ్చాను", "ఎప్పుడు వస్తావ్", "చేరుకున్నా", "రా"),
        "కాల్" to listOf("చేస్తాను", "చేయండి", "మాట్లాడు", "కట్ అయింది", "చేయనా"),
        "మెసేజ్" to listOf("చేయి", "చేశాను", "చూడు", "పెట్టాను", "పంపించు"),
        "సరే" to listOf("అలాగే", "మిత్రమా", "కలుద్దాం", "చూద్దాం", "థాంక్స్"),
        "అవును" to listOf("నిజమే", "కరెక్ట్", "అలాగే", "నేనే", "గుర్తుంది"),
        "కాదు" to listOf("అలా కాదు", "నిజం కాదు", "నాకు తెలీదు", "చేయలేదు", "వద్దు"),
        "అర్జెంట్" to listOf("పని ఉంది", "కాల్ చేయి", "రావాలి", "సహాయం కావాలి", "మేటర్"),
        "డబ్బులు" to listOf("పంపించాను", "ఇచ్చాను", "వచ్చాయా", "కావాలి", "ట్రాన్స్ఫర్"),
        "సమయం" to listOf("ఎంత అయింది", "లేదు", "ముఖ్యమైనది", "బాగుంది", "వచ్చింది")
    )

    // Vowel Phonetics Mapping (Independent & Dependent)
    private val INDEPENDENT_VOWELS = mapOf(
        "aa" to "ఆ", "a" to "అ", "A" to "ఆ",
        "ii" to "ఈ", "ee" to "ఈ", "i" to "ఇ", "I" to "ఈ",
        "uu" to "ఊ", "oo" to "ఊ", "u" to "ఉ", "U" to "ఊ",
        "ru" to "ఋ", "Ru" to "ౠ",
        "ea" to "ఏ", "ae" to "ఏ", "E" to "ఏ", "e" to "ఎ",
        "ai" to "ఐ", "ay" to "ఐ",
        "oa" to "ఓ", "O" to "ఓ", "o" to "ఒ",
        "au" to "ఔ", "ou" to "ఔ",
        "am" to "అం", "aM" to "అం", "aha" to "అః"
    )

    private val DEPENDENT_MATRAS = mapOf(
        "aa" to "ా", "a" to "", "A" to "ా",
        "ii" to "ీ", "ee" to "ీ", "i" to "ి", "I" to "ీ",
        "uu" to "ూ", "oo" to "ూ", "u" to "ు", "U" to "ూ",
        "ru" to "ృ", "Ru" to "ౄ",
        "ea" to "ే", "ae" to "ే", "E" to "ే", "e" to "ె",
        "ai" to "ై", "ay" to "ై",
        "oa" to "ో", "O" to "ో", "o" to "ొ",
        "au" to "ౌ", "ou" to "ౌ",
        "am" to "ం", "aM" to "ం", "aha" to "ః"
    )

    // Consonant Phonetics Mapping
    private val CONSONANTS_MAP = mapOf(
        "ksha" to "క్ష", "kshu" to "క్షు", "kshi" to "క్షి", "ksho" to "క్షో", "ksha" to "క్ష",
        "kkh" to "క్ఖ", "kk" to "క్క",
        "ggh" to "గ్ఘ", "gg" to "గ్గ",
        "cch" to "చ్ఛ", "ccha" to "చ్ఛ", "cchi" to "చ్ఛి", "cc" to "చ్చ",
        "jjh" to "జ్ఝ", "jj" to "జ్జ",
        "tth" to "త్థ", "tt" to "త్త", "ṭṭ" to "ట్ట",
        "ddh" to "ద్ధ", "dd" to "ద్ద",
        "nna" to "న్న", "nnu" to "న్ను", "nni" to "న్ని", "nn" to "న్న",
        "ppa" to "ప్ప", "ppi" to "ప్పి", "ppu" to "ప్పు", "pp" to "ప్ప",
        "bba" to "బ్బ", "bbu" to "బ్బు", "bbi" to "బ్బి", "bb" to "బ్బ",
        "mma" to "మ్మ", "mmi" to "మ్మి", "mmu" to "మ్ము", "mm" to "మ్మ",
        "yya" to "య్య", "yyi" to "య్యి", "yyu" to "య్యు", "yy" to "య్య",
        "rra" to "ర్ర", "rri" to "ర్రి", "rru" to "ర్రు", "rr" to "ర్ర",
        "lla" to "ల్ల", "lli" to "ల్లి", "llu" to "ల్లు", "ll" to "ల్ల",
        "vva" to "వ్వ", "vvi" to "వ్వి", "vvu" to "వ్వు", "vv" to "వ్వ",
        "ssa" to "స్స", "ssi" to "స్సి", "ssu" to "స్సు", "ss" to "స్స",
        "stra" to "స్త్ర", "stri" to "స్త్రి", "stru" to "స్త్రు",
        "pra" to "ప్ర", "pri" to "ప్రి", "pru" to "ప్రు", "pro" to "ప్రో", "pre" to "ప్రె",
        "tra" to "త్ర", "tri" to "త్రి", "tru" to "త్రు",
        "kra" to "క్ర", "kri" to "క్రి", "kru" to "క్రు",
        "gra" to "గ్ర", "gri" to "గ్రి", "gru" to "గ్రు",
        "bra" to "బ్ర", "bri" to "బ్రి", "bru" to "బ్రు",
        "shna" to "శ్న", "shni" to "శ్ని",
        "swa" to "స్వ", "swi" to "స్వి", "swe" to "స్వె",
        "jnya" to "జ్ఞ", "jny" to "జ్ఞ",
        "kh" to "ఖ", "k" to "క", "c" to "చ",
        "gh" to "ఘ", "g" to "గ",
        "ng" to "ఙ", "gn" to "ఙ",
        "chh" to "ఛ", "ch" to "చ",
        "jh" to "ఝ", "j" to "జ",
        "ny" to "ఞ", "nj" to "ఞ",
        "Th" to "ఠ", "th" to "త", "T" to "ట", "t" to "త",
        "Dh" to "ఢ", "dh" to "ద", "D" to "డ", "d" to "ద",
        "N" to "ణ", "n" to "న",
        "ph" to "ఫ", "f" to "ఫ", "p" to "ప",
        "bh" to "భ", "B" to "భ", "b" to "బ",
        "m" to "మ",
        "y" to "య",
        "r" to "ర", "R" to "ఱ",
        "l" to "ల", "L" to "ళ",
        "v" to "వ", "w" to "వ",
        "shh" to "ష", "sh" to "శ", "Sh" to "ష", "s" to "స", "S" to "శ",
        "h" to "హ", "H" to "హ",
        "x" to "క్ష"
    )

    /**
     * Primary transliteration function: returns top 3 suggestions
     * 1. Top Telugu Transliteration
     * 2. Raw English literal string
     * 3. Alternate phonetic spelling or Next-word prediction
     */
    fun transliterate(input: String, previousWord: String? = null): Triple<String, String, String> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            val nextWords = previousWord?.let { getNextWords(it) } ?: listOf("నమస్తే", "బాగున్నారా", "కలుద్దాం")
            return Triple(
                nextWords.getOrNull(0) ?: "నమస్తే",
                nextWords.getOrNull(1) ?: "బాగున్నారా",
                nextWords.getOrNull(2) ?: "కలుద్దాం"
            )
        }

        val cacheKey = "$trimmed|${previousWord.orEmpty()}"
        val cached = TRANSLITERATION_LRU_CACHE.get(cacheKey)
        if (cached != null) {
            return cached
        }

        val lower = trimmed.lowercase()

        // 1. Direct dictionary match
        val result = if (DICTIONARY.containsKey(lower)) {
            val dictMatch = DICTIONARY.getValue(lower)
            val nextPredicted = getNextWords(dictMatch).firstOrNull() ?: alternateTransliterate(trimmed)
            Triple(dictMatch, trimmed, nextPredicted)
        } else {
            // 2. Synthesize via phonetic rule engine
            val ruleTelugu = convertPhonetic(trimmed)

            // 3. Alternative suggestion (e.g. aspirated or hard retroflex variation)
            val alternate = generateAlternate(trimmed, ruleTelugu)

            Triple(ruleTelugu, trimmed, alternate)
        }

        TRANSLITERATION_LRU_CACHE.put(cacheKey, result)
        return result
    }

    /**
     * Converts an English phonetic string to Telugu script.
     */
    fun convertPhonetic(rawInput: String): String {
        val input = rawInput.trim()
        if (input.isEmpty()) return ""

        val sb = StringBuilder()
        var i = 0
        val len = input.length

        while (i < len) {
            // Check for punctuation or digits
            val c = input[i]
            if (c.isDigit()) {
                val digitVal = c - '0'
                if (digitVal in 0..9) {
                    sb.append(TeluguScriptConstants.TELUGU_DIGITS[digitVal])
                } else {
                    sb.append(c)
                }
                i++
                continue
            }
            if (!c.isLetter()) {
                sb.append(c)
                i++
                continue
            }

            // Check multi-character consonant match (up to 4 chars e.g. "ksha", "stra")
            var matchedConsonant: String? = null
            var matchedLen = 0

            for (tryLen in 4 downTo 1) {
                if (i + tryLen <= len) {
                    val sub = input.substring(i, i + tryLen)
                    if (CONSONANTS_MAP.containsKey(sub)) {
                        matchedConsonant = CONSONANTS_MAP[sub]
                        matchedLen = tryLen
                        break
                    }
                }
            }

            if (matchedConsonant != null) {
                i += matchedLen
                // Now look for following vowel/matra
                var matchedMatra: String? = null
                var matraLen = 0

                for (tryLen in 3 downTo 1) {
                    if (i + tryLen <= len) {
                        val sub = input.substring(i, i + tryLen)
                        if (DEPENDENT_MATRAS.containsKey(sub)) {
                            matchedMatra = DEPENDENT_MATRAS[sub]
                            matraLen = tryLen
                            break
                        }
                    }
                }

                if (matchedMatra != null) {
                    sb.append(matchedConsonant).append(matchedMatra)
                    i += matraLen
                } else {
                    // No vowel follows, add virama (halant / పొల్లు) if at end or followed by another consonant
                    if (i < len && input[i].isLetter()) {
                        sb.append(matchedConsonant).append("్")
                    } else {
                        // Default inherent 'a' sound (తలకట్టు)
                        sb.append(matchedConsonant)
                    }
                }
            } else {
                // Independent Vowel at start or after vowel
                var matchedVowel: String? = null
                var vowelLen = 0

                for (tryLen in 3 downTo 1) {
                    if (i + tryLen <= len) {
                        val sub = input.substring(i, i + tryLen)
                        if (INDEPENDENT_VOWELS.containsKey(sub)) {
                            matchedVowel = INDEPENDENT_VOWELS[sub]
                            vowelLen = tryLen
                            break
                        }
                    }
                }

                if (matchedVowel != null) {
                    sb.append(matchedVowel)
                    i += vowelLen
                } else {
                    sb.append(input[i])
                    i++
                }
            }
        }

        return sb.toString()
    }

    private fun alternateTransliterate(input: String): String {
        return convertPhonetic(input)
    }

    private fun generateAlternate(input: String, primaryTelugu: String): String {
        // Disambiguate common alternates: th -> థ vs త, sh -> ష vs శ, t -> ట vs త
        val lower = input.lowercase()
        return when {
            lower.contains("th") -> convertPhonetic(input.replace("th", "Th"))
            lower.contains("sh") -> convertPhonetic(input.replace("sh", "Sh"))
            lower.contains("t") && !lower.contains("th") -> convertPhonetic(input.replace("t", "T"))
            lower.contains("d") && !lower.contains("dh") -> convertPhonetic(input.replace("d", "D"))
            lower.endsWith("u") -> primaryTelugu + "ు"
            else -> primaryTelugu
        }
    }

    /**
     * Next word suggestions based on context.
     */
    fun getNextWords(currentTeluguWord: String): List<String> {
        val trimmed = currentTeluguWord.trim()
        return NEXT_WORD_PREDICTIONS[trimmed] ?: listOf("అండి", "మిత్రమా", "సరే", "చూద్దాం")
    }

    /**
     * Search dictionary entries for Explorer screen.
     */
    fun searchDictionary(query: String): List<Pair<String, String>> {
        val q = query.lowercase().trim()
        if (q.isEmpty()) {
            return DICTIONARY.entries.take(40).map { it.key to it.value }
        }
        return DICTIONARY.entries
            .filter { it.key.contains(q) || it.value.contains(q) }
            .take(50)
            .map { it.key to it.value }
    }
}
