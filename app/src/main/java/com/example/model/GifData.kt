package com.example.model

data class TeluguGifItem(
    val id: String,
    val title: String,
    val category: String,
    val actor: String,
    val captionTelugu: String,
    val emoji: String,
    val accentColorHex: Long,
    val animationFrames: List<String>
)

object GifData {
    val BUNDLED_GIFS = listOf(
        TeluguGifItem(
            id = "brahmi_laugh",
            title = "Brahmanandam Epic Laugh",
            category = "Comedy",
            actor = "Brahmanandam",
            captionTelugu = "హహహ! తట్టుకోలేకపోతున్నా రా బాబు 😂",
            emoji = "🤣",
            accentColorHex = 0xFFFDE047,
            animationFrames = listOf("😂", "🤣", "😆", "😂")
        ),
        TeluguGifItem(
            id = "balayya_roar",
            title = "Balakrishna Lion Roar",
            category = "Mass",
            actor = "Nandamuri Balakrishna",
            captionTelugu = "సింహం బోనులో ఉన్నా బయట ఉన్నా సింహమే! 🦁",
            emoji = "🦁",
            accentColorHex = 0xFFF97316,
            animationFrames = listOf("🦁", "🔥", "⚡", "🦁")
        ),
        TeluguGifItem(
            id = "allu_pushpa_swag",
            title = "Pushpa Thaggedele Walk",
            category = "Mass",
            actor = "Allu Arjun",
            captionTelugu = "పుష్ప అంటే ఫ్లవర్ అనుకుంటివా.. ఫైర్రు! 🔥",
            emoji = "🤙",
            accentColorHex = 0xFFEF4444,
            animationFrames = listOf("🤙", "🕶️", "🔥", "🤙")
        ),
        TeluguGifItem(
            id = "venky_victory_dance",
            title = "Venky Fun Dance",
            category = "Dance",
            actor = "Venkatesh",
            captionTelugu = "అంతేగా అంతేగా! సరదాగా ఉండాలి బ్రో 🕺",
            emoji = "🕺",
            accentColorHex = 0xFF3B82F6,
            animationFrames = listOf("🕺", "✨", "🎉", "🕺")
        ),
        TeluguGifItem(
            id = "mahesh_attitude",
            title = "Mahesh Babu Mass Entry",
            category = "Mass",
            actor = "Mahesh Babu",
            captionTelugu = "ఒక్కసారి కమిట్ అయితే నా మాట నేనే వినను! 💥",
            emoji = "😎",
            accentColorHex = 0xFF6366F1,
            animationFrames = listOf("😎", "💥", "🕶️", "😎")
        ),
        TeluguGifItem(
            id = "pawan_kalyan_swag",
            title = "Power Star Swag Rub",
            category = "Swag",
            actor = "Pawan Kalyan",
            captionTelugu = "నేను ట్రెండ్ ఫాలో అవ్వను.. సెట్ చేస్తా! 🌟",
            emoji = "⭐",
            accentColorHex = 0xFFA855F7,
            animationFrames = listOf("⭐", "✨", "🔥", "⭐")
        ),
        TeluguGifItem(
            id = "ntr_rra_roar",
            title = "Jr NTR High Voltage",
            category = "Mass",
            actor = "Jr NTR",
            captionTelugu = "యమదొంగ దెబ్బ.. చూస్తావా రచ్చ! ⚡",
            emoji = "⚡",
            accentColorHex = 0xFF10B981,
            animationFrames = listOf("⚡", "🐅", "🔥", "⚡")
        ),
        TeluguGifItem(
            id = "prabhas_baahubali",
            title = "Prabhas Baahubali Vow",
            category = "Epic",
            actor = "Prabhas",
            captionTelugu = "జై మాహిష్మతి! నా మాటే శాసనం 👑",
            emoji = "👑",
            accentColorHex = 0xFFEAB308,
            animationFrames = listOf("👑", "🗡️", "🛡️", "👑")
        ),
        TeluguGifItem(
            id = "ali_comedy_shock",
            title = "Ali Shock Reaction",
            category = "Comedy",
            actor = "Ali",
            captionTelugu = "కట్ చేస్తే.. బంపర్ ఆఫర్! 😲",
            emoji = "😲",
            accentColorHex = 0xFFEC4899,
            animationFrames = listOf("😲", "👀", "💥", "😲")
        ),
        TeluguGifItem(
            id = "sunil_dance_energy",
            title = "Sunil Fast Dance",
            category = "Dance",
            actor = "Sunil",
            captionTelugu = "డ్యాన్స్ అంటే ఇలా ఉండాలి మావా! 💃",
            emoji = "💃",
            accentColorHex = 0xFF14B8A6,
            animationFrames = listOf("💃", "🔥", "🎵", "💃")
        ),
        TeluguGifItem(
            id = "chiranjeevi_veena_step",
            title = "Megastar Iconic Veena Step",
            category = "Dance",
            actor = "Chiranjeevi",
            captionTelugu = "మెగాస్టార్ వీణా స్టెప్.. అల్టిమేట్ క్రేజ్! 🎸",
            emoji = "🎸",
            accentColorHex = 0xFFF43F5E,
            animationFrames = listOf("🎸", "⭐", "🕺", "🎸")
        ),
        TeluguGifItem(
            id = "ms_narayana_punch",
            title = "MS Narayana Drunk Class",
            category = "Comedy",
            actor = "MS Narayana",
            captionTelugu = "నా నాలెడ్జ్ ని తక్కువ అంచనా వేయకండి! 🧐",
            emoji = "🧐",
            accentColorHex = 0xFF8B5CF6,
            animationFrames = listOf("🧐", "🥂", "😂", "🧐")
        )
    )
}
