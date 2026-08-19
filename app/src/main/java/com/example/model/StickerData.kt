package com.example.model

data class TeluguMemeSticker(
    val id: String,
    val title: String,
    val punchlineTelugu: String,
    val characterName: String,
    val emoji: String,
    val bgColorHex: Long,
    val fgColorHex: Long
)

object StickerData {
    val BUNDLED_STICKERS = listOf(
        TeluguMemeSticker(
            id = "brahmi_shock",
            title = "Brahmi Shocked",
            punchlineTelugu = "ఎంత మాట అన్నావ్ రా!",
            characterName = "Brahmanandam",
            emoji = "😲",
            bgColorHex = 0xFFFFE4E6,
            fgColorHex = 0xFF9F1239
        ),
        TeluguMemeSticker(
            id = "brahmi_namaste",
            title = "Namaskaram",
            punchlineTelugu = "నమస్కారం బాస్.. ఎప్పుడు వచ్చారు?",
            characterName = "Brahmanandam",
            emoji = "🙏",
            bgColorHex = 0xFFFEF3C7,
            fgColorHex = 0xFF92400E
        ),
        TeluguMemeSticker(
            id = "ali_adirindi",
            title = "Adiripoyindi",
            punchlineTelugu = "సినిమా అదిరిపోయింది రోయ్!",
            characterName = "Ali",
            emoji = "🔥",
            bgColorHex = 0xFFDCFCE7,
            fgColorHex = 0xFF166534
        ),
        TeluguMemeSticker(
            id = "sunil_doubt",
            title = "Sunil Confusion",
            punchlineTelugu = "నాకేదో తేడాగా అనిపిస్తుంది మావా!",
            characterName = "Sunil",
            emoji = "🤨",
            bgColorHex = 0xFFE0E7FF,
            fgColorHex = 0xFF3730A3
        ),
        TeluguMemeSticker(
            id = "ms_narayana_saradaga",
            title = "MS Fun",
            punchlineTelugu = "సరదాగా కాసేపు నవ్వుకుందాం!",
            characterName = "MS Narayana",
            emoji = "😂",
            bgColorHex = 0xFFF3E8FF,
            fgColorHex = 0xFF6B21A8
        ),
        TeluguMemeSticker(
            id = "relangi_babu",
            title = "Relangi Smile",
            punchlineTelugu = "మంచి మనసుతో నవ్వుతూ ఉండు బాబు!",
            characterName = "Relangi",
            emoji = "😊",
            bgColorHex = 0xFFECFCCB,
            fgColorHex = 0xFF3F6212
        ),
        TeluguMemeSticker(
            id = "brahmi_evadra",
            title = "Evadra Nuvvu",
            punchlineTelugu = "అసలు ఎవడ్రా నువ్వు.. ఇక్కడ ఏం పని?",
            characterName = "Brahmanandam",
            emoji = "🧐",
            bgColorHex = 0xFFFFEDD5,
            fgColorHex = 0xFF9A3412
        ),
        TeluguMemeSticker(
            id = "venky_aasan",
            title = "Venky Asan",
            punchlineTelugu = "అంతా ప్రశాంతంగా ఉండాలి అంతే!",
            characterName = "Venkatesh",
            emoji = "🧘‍♂️",
            bgColorHex = 0xFFCFFAFE,
            fgColorHex = 0xFF155E75
        ),
        TeluguMemeSticker(
            id = "puri_dialogue",
            title = "Gunapam",
            punchlineTelugu = "ఒకసారి కమిట్ అయితే నా మాట నేనే వినను!",
            characterName = "Mahesh Babu",
            emoji = "💥",
            bgColorHex = 0xFFFEE2E2,
            fgColorHex = 0xFF991B1B
        ),
        TeluguMemeSticker(
            id = "balayya_thigh_slap",
            title = "Balayya Swag",
            punchlineTelugu = "ఫ్లూట్ జింక ముందు ఊదు.. సింహం ముందు కాదు!",
            characterName = "Balakrishna",
            emoji = "🦁",
            bgColorHex = 0xFFFEF9C3,
            fgColorHex = 0xFF854D0E
        ),
        TeluguMemeSticker(
            id = "chiranjeevi_keka",
            title = "Megastar Keka",
            punchlineTelugu = "కుమ్మేసావ్ అంతే.. కేక పుట్టించావ్!",
            characterName = "Chiranjeevi",
            emoji = "⭐",
            bgColorHex = 0xFFE0F2FE,
            fgColorHex = 0xFF075985
        ),
        TeluguMemeSticker(
            id = "allu_taggede_le",
            title = "Thaggede Le",
            punchlineTelugu = "తగ్గేదే లే! పుష్ప రాజ్!",
            characterName = "Allu Arjun",
            emoji = "🤙",
            bgColorHex = 0xFFFCE7F3,
            fgColorHex = 0xFF9D174D
        )
    )
}
