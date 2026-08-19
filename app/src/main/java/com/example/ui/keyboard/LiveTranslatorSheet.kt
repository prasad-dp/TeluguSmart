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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Translate
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.TeluguTranslatorEngine

enum class TranslationDirection(val sourceLabel: String, val targetLabel: String) {
    ENG_TO_TELUGU("English", "తెలుగు (Telugu)"),
    TELUGU_TO_ENG("తెలుగు (Telugu)", "English")
}

val COMMON_TRANSLATE_QUICK_PHRASES = listOf(
    "How are you?",
    "Where are you?",
    "I am coming",
    "Call me please",
    "Good morning",
    "Thank you so much",
    "What are you doing?",
    "See you soon"
)

@Composable
fun LiveTranslatorSheet(
    modifier: Modifier = Modifier,
    palette: KeyboardPalette,
    initialInput: String = "",
    onCommitTranslation: (String) -> Unit,
    onClose: () -> Unit
) {
    var direction by remember { mutableStateOf(TranslationDirection.ENG_TO_TELUGU) }
    var inputText by remember { mutableStateOf(initialInput.ifBlank { "How are you?" }) }

    val liveTranslation = remember(inputText, direction) {
        if (direction == TranslationDirection.ENG_TO_TELUGU) {
            TeluguTranslatorEngine.translateEnglishToTelugu(inputText)
        } else {
            TeluguTranslatorEngine.translateTeluguToEnglish(inputText)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(295.dp)
            .background(palette.surface)
            .padding(10.dp)
    ) {
        // Header & Language Switcher
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
                    imageVector = Icons.Default.Translate,
                    contentDescription = null,
                    tint = palette.accent,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Live Chat Translator (లైవ్ అనువాదం)",
                    color = palette.keyText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier.size(26.dp).testTag("close_translator_sheet")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = palette.keySecondaryText
                )
            }
        }

        // Language Direction Selector Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = palette.background,
                border = androidx.compose.foundation.BorderStroke(1.dp, palette.border)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = direction.sourceLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.accent
                    )
                    IconButton(
                        onClick = {
                            direction = if (direction == TranslationDirection.ENG_TO_TELUGU) {
                                TranslationDirection.TELUGU_TO_ENG
                            } else {
                                TranslationDirection.ENG_TO_TELUGU
                            }
                            inputText = liveTranslation
                        },
                        modifier = Modifier.size(22.dp).testTag("swap_translate_direction")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Swap Direction",
                            tint = palette.keyText,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = direction.targetLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.keyText
                    )
                }
            }
        }

        // Input Field
        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            placeholder = { Text("Type text to translate live...", fontSize = 11.sp, color = palette.keySecondaryText) },
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

        // Quick Phrases Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            COMMON_TRANSLATE_QUICK_PHRASES.forEach { phrase ->
                Surface(
                    onClick = {
                        direction = TranslationDirection.ENG_TO_TELUGU
                        inputText = phrase
                    },
                    shape = RoundedCornerShape(8.dp),
                    color = palette.keyBackground,
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, palette.border)
                ) {
                    Text(
                        text = phrase,
                        fontSize = 10.sp,
                        color = palette.keySecondaryText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // Translation Result Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(palette.background)
                .border(1.dp, palette.accent.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "TRANSLATED RESULT (${if (direction == TranslationDirection.ENG_TO_TELUGU) "తెలుగు" else "English"}):",
                    fontSize = 9.sp,
                    color = palette.accent,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = liveTranslation.ifBlank { "..." },
                    fontSize = 14.sp,
                    color = palette.keyText,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Commit Button
        Button(
            onClick = {
                if (liveTranslation.isNotBlank()) {
                    onCommitTranslation(liveTranslation + " ")
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = palette.accent,
                contentColor = palette.accentText
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp)
                .testTag("insert_translation_btn")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                Text(
                    text = "Insert Translation (${liveTranslation.take(15)}...)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
