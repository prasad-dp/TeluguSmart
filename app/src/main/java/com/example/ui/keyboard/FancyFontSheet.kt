package com.example.ui.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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

object FancyFontConverter {

    data class FontStyle(val name: String, val transform: (String) -> String)

    val STYLES = listOf(
        FontStyle("𝗕𝗼𝗹𝗱 𝗦𝗮𝗻𝘀") { text -> convertMath(text, 0x1D5D4, 0x1D5EE, 0x1D7EC) },
        FontStyle("𝘐𝘵𝘢𝘭𝘪𝘤 𝘚𝘢𝘯𝘴") { text -> convertMath(text, 0x1D608, 0x1D622, null) },
        FontStyle("𝓒𝓾𝓻𝓼𝓲𝓿𝓮 𝓢𝓬𝓻𝓲𝓹𝓽") { text -> convertMath(text, 0x1D4D0, 0x1D4EA, null) },
        FontStyle("𝔉𝔯𝔞𝔨𝔱𝔲𝔯 𝔊𝔬𝔱𝔥𝔦𝔠") { text -> convertMath(text, 0x1D504, 0x1D51E, null) },
        FontStyle("🅂🅀🅄🄰🅁🄴🄳") { text ->
            text.map { c ->
                when (c) {
                    in 'a'..'z' -> String(Character.toChars(0x1F130 + (c - 'a')))
                    in 'A'..'Z' -> String(Character.toChars(0x1F130 + (c - 'A')))
                    else -> c.toString()
                }
            }.joinToString("")
        },
        FontStyle("Ⓑⓤⓑⓑⓛⓔ Ⓒⓘⓡⓒⓛⓔ") { text ->
            text.map { c ->
                when (c) {
                    in 'a'..'z' -> String(Character.toChars(0x24D0 + (c - 'a')))
                    in 'A'..'Z' -> String(Character.toChars(0x24B6 + (c - 'A')))
                    in '0'..'9' -> if (c == '0') "⓪" else String(Character.toChars(0x2460 + (c - '1')))
                    else -> c.toString()
                }
            }.joinToString("")
        },
        FontStyle("𝙼𝚘𝚗𝚘𝚜𝚙𝚊𝚌𝚎") { text -> convertMath(text, 0x1D670, 0x1D68A, 0x1D7F6) },
        FontStyle("🆂🆀🆄🅰🆁🅴 🅵🅸🅻🅻") { text ->
            text.map { c ->
                when (c) {
                    in 'a'..'z' -> String(Character.toChars(0x1F170 + (c - 'a')))
                    in 'A'..'Z' -> String(Character.toChars(0x1F170 + (c - 'A')))
                    else -> c.toString()
                }
            }.joinToString("")
        },
        FontStyle("✨ S p a r k l e ✨") { text -> "✨ $text ✨" },
        FontStyle("★ 𝒯ℯ𝓁𝓊ℊ𝓊 ★") { text -> "★ $text ★" },
        FontStyle("꧁༺ 𝒫𝓇ℴ ༻꧂") { text -> "꧁༺ $text ༻꧂" }
    )

    private fun convertMath(text: String, upperStart: Int, lowerStart: Int, digitStart: Int?): String {
        return text.map { c ->
            when (c) {
                in 'A'..'Z' -> String(Character.toChars(upperStart + (c - 'A')))
                in 'a'..'z' -> String(Character.toChars(lowerStart + (c - 'a')))
                in '0'..'9' -> if (digitStart != null) String(Character.toChars(digitStart + (c - '0'))) else c.toString()
                else -> c.toString()
            }
        }.joinToString("")
    }
}

@Composable
fun FancyFontSheet(
    modifier: Modifier = Modifier,
    palette: KeyboardPalette,
    initialText: String,
    onSelectStyledText: (String) -> Unit,
    onClose: () -> Unit
) {
    var inputText by remember { mutableStateOf(if (initialText.isBlank()) "Telugu Smart" else initialText) }

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
                    text = "Fancy Font Styles (స్టైలిష్ ఫాంట్లు)",
                    color = palette.keyText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tap any style to paste styled decorative typography",
                    color = palette.keySecondaryText,
                    fontSize = 10.sp
                )
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier.size(28.dp).testTag("close_fancy_font_sheet")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Fonts",
                    tint = palette.keySecondaryText
                )
            }
        }

        // Input text to transform
        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            placeholder = { Text("Type text to convert to fancy fonts...", fontSize = 11.sp, color = palette.keySecondaryText) },
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

        Spacer(modifier = Modifier.height(4.dp))

        // Styles List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(FancyFontConverter.STYLES) { style ->
                val transformed = remember(inputText, style) {
                    try {
                        style.transform(if (inputText.isBlank()) "Telugu" else inputText)
                    } catch (_: Exception) {
                        inputText
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(palette.keyBackground)
                        .clickable { onSelectStyledText(transformed) }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .testTag("fancy_font_item"),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = transformed,
                        color = palette.keyText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = style.name,
                        color = palette.keySecondaryText,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}
