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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.TeluguTransliterationEngine

data class StickerStylePreset(
    val name: String,
    val bgGradient: List<Color>,
    val textColor: Color,
    val badgeBorder: Color,
    val defaultEmoji: String
)

val STICKER_PRESETS = listOf(
    StickerStylePreset(
        name = "🔥 Fire Mass",
        bgGradient = listOf(Color(0xFF7F1D1D), Color(0xFFEF4444)),
        textColor = Color(0xFFFEF2F2),
        badgeBorder = Color(0xFFFCA5A5),
        defaultEmoji = "🔥"
    ),
    StickerStylePreset(
        name = "👑 Royal Gold",
        bgGradient = listOf(Color(0xFF713F12), Color(0xFFEAB308)),
        textColor = Color(0xFFFEFCE8),
        badgeBorder = Color(0xFFFDE047),
        defaultEmoji = "👑"
    ),
    StickerStylePreset(
        name = "🤣 Brahmi Comedy",
        bgGradient = listOf(Color(0xFF14532D), Color(0xFF22C55E)),
        textColor = Color(0xFFF0FDF4),
        badgeBorder = Color(0xFF86EFAC),
        defaultEmoji = "🤣"
    ),
    StickerStylePreset(
        name = "⚡ Cyber Neon",
        bgGradient = listOf(Color(0xFF3B0764), Color(0xFFA855F7)),
        textColor = Color(0xFFFAF5FF),
        badgeBorder = Color(0xFFE9D5FF),
        defaultEmoji = "⚡"
    ),
    StickerStylePreset(
        name = "🌊 Ocean Swag",
        bgGradient = listOf(Color(0xFF0C4A6E), Color(0xFF0EA5E9)),
        textColor = Color(0xFFF0F9FF),
        badgeBorder = Color(0xFF7DD3FC),
        defaultEmoji = "🤙"
    ),
    StickerStylePreset(
        name = "🌸 Rose Love",
        bgGradient = listOf(Color(0xFF831843), Color(0xFFEC4899)),
        textColor = Color(0xFFFDF2F8),
        badgeBorder = Color(0xFFFBCFE8),
        defaultEmoji = "💖"
    )
)

val POPULAR_PUNCHLINES = listOf(
    "తగ్గేదే లే! 🤙",
    "బాబాయ్ వచ్చాడు! 🔥",
    "అంతేగా అంతేగా! 🤣",
    "చూస్తావా రచ్చ! ⚡",
    "ఒక్కసారి కమిట్ అయితే.. 😎",
    "బంపర్ ఆఫర్ బ్రో! 💥",
    "సరే సర్లే ఏం చేస్తాం 🤷‍♂️",
    "జై మాహిష్మతి! 👑"
)

val EMOJI_OPTIONS = listOf("🔥", "👑", "🤣", "🤙", "😎", "💥", "⚡", "💖", "🙏", "🕺", "🤯", "🥳")

@Composable
fun CustomStickerMakerSheet(
    modifier: Modifier = Modifier,
    palette: KeyboardPalette,
    initialText: String = "",
    onCommitSticker: (String) -> Unit,
    onClose: () -> Unit
) {
    var rawInputText by remember { mutableStateOf(initialText.ifBlank { "తగ్గేదే లే!" }) }
    var selectedPreset by remember { mutableStateOf(STICKER_PRESETS[0]) }
    var selectedEmoji by remember { mutableStateOf(STICKER_PRESETS[0].defaultEmoji) }

    // Instant transliteration for user convenience if typed in English
    val displayTeluguText = remember(rawInputText) {
        if (rawInputText.any { it in 'a'..'z' || it in 'A'..'Z' }) {
            TeluguTransliterationEngine.transliterate(rawInputText).first.ifEmpty { rawInputText }
        } else {
            rawInputText
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
                    text = "Custom Telugu Meme Sticker Maker (స్టిక్కర్ మేకర్)",
                    color = palette.keyText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier.size(26.dp).testTag("close_sticker_maker")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = palette.keySecondaryText
                )
            }
        }

        // Live Sticker Preview Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.horizontalGradient(selectedPreset.bgGradient))
                    .border(2.dp, selectedPreset.badgeBorder, RoundedCornerShape(16.dp))
                    .shadow(8.dp, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .testTag("sticker_preview_badge"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = selectedEmoji,
                        fontSize = 28.sp
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = displayTeluguText.ifBlank { "మీ డైలాగ్ ఇక్కడ రాయండి" },
                            color = selectedPreset.textColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "★ TELUGU STICKER ★",
                            color = selectedPreset.textColor.copy(alpha = 0.75f),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        // Input Field for custom dialogue
        OutlinedTextField(
            value = rawInputText,
            onValueChange = { rawInputText = it },
            placeholder = { Text("Type Telugu / Tenglish punchline (e.g. babai vachadu)...", fontSize = 11.sp, color = palette.keySecondaryText) },
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

        // Quick Dialogue Suggestions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            POPULAR_PUNCHLINES.forEach { punch ->
                Surface(
                    onClick = { rawInputText = punch },
                    shape = RoundedCornerShape(10.dp),
                    color = palette.keyBackground,
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, palette.border)
                ) {
                    Text(
                        text = punch,
                        fontSize = 10.sp,
                        color = palette.keyText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // Style Themes & Emojis Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Style Presets
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(STICKER_PRESETS) { preset ->
                    val isSelected = preset == selectedPreset
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(preset.bgGradient))
                            .border(if (isSelected) 2.dp else 0.5.dp, if (isSelected) Color.White else Color.Transparent, CircleShape)
                            .clickable {
                                selectedPreset = preset
                                selectedEmoji = preset.defaultEmoji
                            }
                    )
                }
            }

            // Emoji Selection
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(EMOJI_OPTIONS) { emoji ->
                    val isSelected = emoji == selectedEmoji
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) palette.accent else palette.keyBackground)
                            .clickable { selectedEmoji = emoji },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 13.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Commit Sticker Button
        Button(
            onClick = {
                val formattedSticker = "✨ [$selectedEmoji $displayTeluguText] ✨"
                onCommitSticker(formattedSticker)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = palette.accent,
                contentColor = palette.accentText
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp)
                .testTag("insert_custom_sticker_btn")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(15.dp))
                Text(
                    text = "Insert Custom Sticker Card",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
