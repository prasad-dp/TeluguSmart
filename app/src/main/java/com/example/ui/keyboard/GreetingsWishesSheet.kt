package com.example.ui.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TeluguPhraseCategory(
    val title: String,
    val emoji: String,
    val phrases: List<String>
)

object TeluguPhrasesData {
    val CATEGORIES = listOf(
        TeluguPhraseCategory(
            title = "శుభోదయం / రాత్రి",
            emoji = "🌅",
            phrases = listOf(
                "శుభోదయం! ఈ రోజు మీకు అంతా మంచే జరగాలి ☀️",
                "శుభోదయం మిత్రమా! హ్యాపీ డే 🌸",
                "శుభ మధ్యాహ్నం! భోజనం చేశారా? 🍲",
                "శుభ సాయంత్రం! కాఫీ తాగారా? ☕",
                "శుభరాత్రి! తీపి కలలు కనండి 🌙✨",
                "గుడ్ నైట్.. రేపు కలుద్దాం 😴"
            )
        ),
        TeluguPhraseCategory(
            title = "శుభాకాంక్షలు & పండుగలు",
            emoji = "🎉",
            phrases = listOf(
                "మీకు మరియు మీ కుటుంబ సభ్యులకు హృదయపూర్వక జన్మదిన శుభాకాంక్షలు! 🎂🎉",
                "వివాహ వార్షికోత్సవ శుభాకాంక్షలు! నిండు నూరేళ్లు సుఖసంతోషాలతో వర్ధిల్లాలి 💐💑",
                "సంక్రాంతి మరియు భోగి పండుగ శుభాకాంక్షలు! 🪁🌾",
                "దీపావళి పండుగ శుభాకాంక్షలు! మీ ఇంట లక్ష్మీ కటాక్షం సిద్ధించుగాక 🪔✨",
                "ఉగాది పండుగ శుభాకాంక్షలు! 🌸🥭",
                "విజయదశమి (దసరా) శుభాకాంక్షలు! 🏹✨",
                "నూతన సంవత్సర శుభాకాంక్షలు! (Happy New Year) 🎆🥳",
                "మీ జీవితంలో అనుకున్న ప్రతి లక్ష్యం నెరవేరాలని మనసారా కోరుకుంటున్నాను! 🌟"
            )
        ),
        TeluguPhraseCategory(
            title = "నిత్య జీవిత సంభాషణలు",
            emoji = "💬",
            phrases = listOf(
                "నమస్కారం! ఎలా ఉన్నారు? బాగున్నారా?",
                "ఏంటి విశేషాలు? అంతా కుశలమేనా?",
                "భోజనం చేశారా? ఏం కూర వండారు?",
                "నేను కొంచెం బిజీగా ఉన్నాను, కాసేపట్లో కాల్ చేస్తాను.",
                "మీరు ఎక్కడ ఉన్నారు? ఎప్పుడు వస్తారు?",
                "సరే అండి, రేపు తప్పకుండా కలుద్దాం.",
                "మీరు చెప్పింది చాలా కరెక్ట్!",
                "జాగ్రత్తగా వెళ్ళి రండి, ఇంటికి చేరాక మెసేజ్ చేయండి."
            )
        ),
        TeluguPhraseCategory(
            title = "మర్యాద & ధన్యవాదాలు",
            emoji = "🙏",
            phrases = listOf(
                "చాలా చాలా ధన్యవాదాలు! 🙏",
                "మీ సహాయానికి నా హృదయపూర్వక కృతజ్ఞతలు.",
                "ఏమీ పర్లేదు, పర్వాలేదు అండి!",
                "నన్ను క్షమించండి, పొరపాటు జరిగింది.",
                "మీకు శుభం కలుగుగాక! ఆల్ ది బెస్ట్ 👍",
                "స్వాగతం సుస్వాగతం! 🌺"
            )
        ),
        TeluguPhraseCategory(
            title = "తెలుగు సామెతలు & సూక్తులు",
            emoji = "📜",
            phrases = listOf(
                "మొక్కై వంగనిది మానై వంగునా?",
                "నిదానమే ప్రధానం, తొందరపాటు పనికిరాదు.",
                "మనసు ఉంటే మార్గం ఉంటుంది.",
                "కూర్చుని తింటే కొండలైనా కరుగుతాయి.",
                "చెప్పుకోదగ్గ మంచి పనులు చేయడమే అసలైన విజయం.",
                "సత్యమేవ జయతే! ధర్మమే అంతిమంగా గెలుస్తుంది."
            )
        )
    )
}

@Composable
fun GreetingsWishesSheet(
    modifier: Modifier = Modifier,
    palette: KeyboardPalette,
    onSelectPhrase: (String) -> Unit,
    onClose: () -> Unit
) {
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    val currentCategory = TeluguPhrasesData.CATEGORIES.getOrElse(selectedCategoryIndex) { TeluguPhrasesData.CATEGORIES[0] }

    val filteredPhrases = remember(searchQuery, selectedCategoryIndex) {
        if (searchQuery.isBlank()) {
            currentCategory.phrases
        } else {
            TeluguPhrasesData.CATEGORIES.flatMap { it.phrases }.filter {
                it.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(palette.surface)
            .padding(8.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Telugu Greetings & Quick Phrases (శుభాకాంక్షలు)",
                    color = palette.keyText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "1-tap paste wishes, greetings & daily Telugu sayings",
                    color = palette.keySecondaryText,
                    fontSize = 10.sp
                )
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier.size(28.dp).testTag("close_greetings_sheet")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Greetings",
                    tint = palette.keySecondaryText
                )
            }
        }

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search wishes (e.g. Birthday, శుభోదయం)...", fontSize = 11.sp, color = palette.keySecondaryText) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = palette.keySecondaryText, modifier = Modifier.size(16.dp))
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(vertical = 2.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = palette.accent,
                unfocusedBorderColor = palette.border,
                focusedTextColor = palette.keyText,
                unfocusedTextColor = palette.keyText,
                focusedContainerColor = palette.background,
                unfocusedContainerColor = palette.background
            ),
            shape = RoundedCornerShape(8.dp)
        )

        // Category Pills (Horizontal Scroll)
        if (searchQuery.isBlank()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TeluguPhrasesData.CATEGORIES.forEachIndexed { index, cat ->
                    val isSelected = selectedCategoryIndex == index
                    Surface(
                        onClick = { selectedCategoryIndex = index },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) palette.accent else palette.keyBackground,
                        modifier = Modifier.testTag("phrase_cat_$index")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = cat.emoji, fontSize = 11.sp)
                            Text(
                                text = cat.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) palette.accentText else palette.keyText
                            )
                        }
                    }
                }
            }
        }

        // Phrases List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(filteredPhrases) { phrase ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(palette.keyBackground)
                        .clickable { onSelectPhrase(phrase) }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .testTag("phrase_item"),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = phrase,
                        color = palette.keyText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
