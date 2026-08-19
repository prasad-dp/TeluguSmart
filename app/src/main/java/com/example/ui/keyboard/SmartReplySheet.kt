package com.example.ui.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
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
import com.example.engine.TeluguToneType
import com.example.engine.ToneRephraser

data class SmartReplyCategory(
    val id: String,
    val title: String,
    val icon: String,
    val suggestions: List<String>
)

val SMART_REPLY_CATEGORIES = listOf(
    SmartReplyCategory(
        id = "chat_quick",
        title = "Quick Replies (త్వరిత)",
        icon = "⚡",
        suggestions = listOf(
            "అవును, తప్పకుండా చేస్తాను 👍",
            "సరే, నేను 10 నిమిషాల్లో వస్తాను",
            "ఇప్పుడే బయల్దేరాను, చేరుకుంటున్నాను",
            "నేను కొంచెం బిజీగా ఉన్నాను, తర్వాత మాట్లాడతాను",
            "చాలా ధన్యవాదాలు మిత్రమా! 🙏",
            "సూపర్! చాలా బాగుంది 👌",
            "క్షమించండి, ఈరోజు కుదరదు"
        )
    ),
    SmartReplyCategory(
        id = "office_formal",
        title = "Office & Formal (ఆఫీస్)",
        icon = "💼",
        suggestions = listOf(
            "సరే సార్, నేను పరిశీలించి త్వరలోనే సమాచారం అందిస్తాను.",
            "ధన్యవాదాలు, అవసరమైన ఫైల్స్ ఇప్పుడే ఈమెయిల్ చేస్తున్నాను.",
            "ఈ ప్రాజెక్ట్ వివరాలను సమీక్షించి అప్‌డేట్ చేస్తాను.",
            "దయచేసి తదుపరి సమావేశ సమయం తెలియజేయగలరు.",
            "మీ సహాయానికి చాలా కృతజ్ఞతలు."
        )
    ),
    SmartReplyCategory(
        id = "elders_respect",
        title = "Elders Respect (పెద్దలకు)",
        icon = "🙏",
        suggestions = listOf(
            "నమస్కారం అండి, మీ ఆశీస్సులు ఎల్లప్పుడూ మాపై ఉండాలి 🙏",
            "మీరు చెప్పినట్లే తప్పకుండా చేస్తాను అండి.",
            "మీ ఆరోగ్యం ఎలా ఉంది అండి? జాగ్రత్తగా ఉండండి.",
            "తప్పకుండా త్వరలోనే ఇంటికి వచ్చి కలుస్తాను అండి."
        )
    ),
    SmartReplyCategory(
        id = "witty_banter",
        title = "Friends & Banter (సరదా)",
        icon = "🤣",
        suggestions = listOf(
            "అంతేగా అంతేగా! ఇక రచ్చ రంబోలా 🔥",
            "బాబాయ్ వచ్చాడు.. ఇక చూసుకోండి! 😎",
            "సరే సర్లే ఏం చేస్తాం.. నవ్వుకోవడమే 😂",
            "మనం ట్రెండ్ ఫాలో అవ్వం బ్రో.. సెట్ చేస్తాం! 🤙",
            "ఏంట్రా బాబు ఇది.. అసలు ఊహించలేదు 🤯"
        )
    ),
    SmartReplyCategory(
        id = "festive_wishes",
        title = "Wishes (శుభాకాంక్షలు)",
        icon = "🎉",
        suggestions = listOf(
            "మీకు మరియు మీ కుటుంబ సభ్యులకు హృదయపూర్వక శుభాకాంక్షలు! 🪔",
            "జన్మదిన శుభాకాంక్షలు! ఆయురారోగ్యాలతో ఆనందంగా ఉండాలని కోరుకుంటున్నాను 🎂",
            "విజయవంతమైన భవిష్యత్తు కోసం ఆల్ ది బెస్ట్! ✨"
        )
    )
)

@Composable
fun SmartReplySheet(
    modifier: Modifier = Modifier,
    palette: KeyboardPalette,
    currentText: String,
    onSelectReply: (String) -> Unit,
    onClose: () -> Unit
) {
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    var userPrompt by remember { mutableStateOf(currentText) }
    var selectedTone by remember { mutableStateOf(TeluguToneType.FORMAL) }

    val activeCategory = SMART_REPLY_CATEGORIES[selectedCategoryIndex]

    // AI Rephrased output if prompt entered
    val dynamicAiSuggestions = remember(userPrompt, selectedTone) {
        if (userPrompt.isNotBlank()) {
            listOf(
                ToneRephraser.rephraseText(userPrompt, selectedTone),
                ToneRephraser.rephraseText(userPrompt, TeluguToneType.CASUAL),
                ToneRephraser.rephraseText(userPrompt, TeluguToneType.FORMAL),
                ToneRephraser.rephraseText(userPrompt, TeluguToneType.POETIC)
            ).distinct()
        } else {
            emptyList()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(295.dp)
            .background(palette.surface)
            .padding(10.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = palette.accent,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "AI Telugu Smart Reply & Assistant (స్మార్ట్ ప్రత్యుత్తరాలు)",
                    color = palette.keyText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier.size(26.dp).testTag("close_smart_reply_sheet")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = palette.keySecondaryText
                )
            }
        }

        // Search / Prompt bar
        OutlinedTextField(
            value = userPrompt,
            onValueChange = { userPrompt = it },
            placeholder = { Text("Enter sentence or topic to generate smart replies...", fontSize = 11.sp, color = palette.keySecondaryText) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .padding(vertical = 1.dp),
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

        // Category Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SMART_REPLY_CATEGORIES.forEachIndexed { index, category ->
                val isSelected = selectedCategoryIndex == index && userPrompt.isBlank()
                Surface(
                    onClick = {
                        selectedCategoryIndex = index
                        userPrompt = ""
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) palette.accent else palette.keyBackground,
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, palette.border),
                    modifier = Modifier.testTag("reply_cat_${category.id}")
                ) {
                    Text(
                        text = "${category.icon} ${category.title}",
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) palette.accentText else palette.keyText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // List of Smart Reply Cards
        val repliesToShow = if (userPrompt.isNotBlank() && dynamicAiSuggestions.isNotEmpty()) {
            dynamicAiSuggestions
        } else {
            activeCategory.suggestions
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(repliesToShow) { reply ->
                SmartReplyItem(
                    text = reply,
                    palette = palette,
                    onSelect = { onSelectReply(reply) }
                )
            }
        }
    }
}

@Composable
private fun SmartReplyItem(
    text: String,
    palette: KeyboardPalette,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(palette.keyBackground)
            .border(0.5.dp, palette.border, RoundedCornerShape(8.dp))
            .clickable(onClick = onSelect)
            .padding(horizontal = 10.dp, vertical = 7.dp)
            .testTag("smart_reply_item"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = palette.keyText,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Send",
            tint = palette.accent,
            modifier = Modifier.size(14.dp)
        )
    }
}
