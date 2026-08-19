package com.example.ui.keyboard

import android.view.HapticFeedbackConstants
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Gif
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SuggestionStrip(
    modifier: Modifier = Modifier,
    palette: KeyboardPalette,
    layoutMode: KeyboardLayoutMode = KeyboardLayoutMode.TENGLISH,
    currentWord: String,
    teluguSuggestion: String,
    englishSuggestion: String,
    alternateSuggestion: String,
    activePanel: KeyboardPanel,
    onSelectSuggestion: (String) -> Unit,
    onTogglePanel: (KeyboardPanel) -> Unit,
    onSwitchLayout: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(palette.suggestionBackground)
    ) {
        // Quick Action Utility Toolbar (Desh Keyboard Pro Bar)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // 1. Live Chat Translator
                IconButton(
                    onClick = {
                        onTogglePanel(
                            if (activePanel == KeyboardPanel.TRANSLATOR) KeyboardPanel.NONE else KeyboardPanel.TRANSLATOR
                        )
                    },
                    modifier = Modifier.size(30.dp).testTag("action_translator")
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = "Live Chat Translator",
                        tint = if (activePanel == KeyboardPanel.TRANSLATOR) palette.accent else palette.keySecondaryText,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // 2. AI Smart Reply & Tone Assistant
                IconButton(
                    onClick = {
                        onTogglePanel(
                            if (activePanel == KeyboardPanel.SMART_REPLY) KeyboardPanel.NONE else KeyboardPanel.SMART_REPLY
                        )
                    },
                    modifier = Modifier.size(30.dp).testTag("action_smart_reply")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Smart Replies & Tone Assistant",
                        tint = if (activePanel == KeyboardPanel.SMART_REPLY || activePanel == KeyboardPanel.GEN_AI_TONE) palette.accent else palette.keySecondaryText,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // 3. Voice Speech-to-Text
                IconButton(
                    onClick = {
                        onTogglePanel(
                            if (activePanel == KeyboardPanel.VOICE) KeyboardPanel.NONE else KeyboardPanel.VOICE
                        )
                    },
                    modifier = Modifier.size(30.dp).testTag("action_voice")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Speech-to-Text",
                        tint = if (activePanel == KeyboardPanel.VOICE) palette.accent else palette.keySecondaryText,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // 4. Custom Sticker Maker & Telugu Memes
                IconButton(
                    onClick = {
                        onTogglePanel(
                            if (activePanel == KeyboardPanel.CUSTOM_STICKER_MAKER || activePanel == KeyboardPanel.STICKERS) KeyboardPanel.NONE else KeyboardPanel.CUSTOM_STICKER_MAKER
                        )
                    },
                    modifier = Modifier.size(30.dp).testTag("action_custom_sticker_maker")
                ) {
                    Icon(
                        imageVector = Icons.Default.Style,
                        contentDescription = "Custom Meme Sticker Maker",
                        tint = if (activePanel == KeyboardPanel.CUSTOM_STICKER_MAKER || activePanel == KeyboardPanel.STICKERS) palette.accent else palette.keySecondaryText,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // 5. Telugu Animated GIFs (Desh Feature)
                IconButton(
                    onClick = {
                        onTogglePanel(
                            if (activePanel == KeyboardPanel.GIF_PICKER) KeyboardPanel.NONE else KeyboardPanel.GIF_PICKER
                        )
                    },
                    modifier = Modifier.size(30.dp).testTag("action_gifs")
                ) {
                    Icon(
                        imageVector = Icons.Default.Gif,
                        contentDescription = "Telugu Animated GIFs",
                        tint = if (activePanel == KeyboardPanel.GIF_PICKER) palette.accent else palette.keySecondaryText,
                        modifier = Modifier.size(19.dp)
                    )
                }

                // 6. Telugu Greetings & Wishes
                IconButton(
                    onClick = {
                        onTogglePanel(
                            if (activePanel == KeyboardPanel.GREETINGS_WISHES) KeyboardPanel.NONE else KeyboardPanel.GREETINGS_WISHES
                        )
                    },
                    modifier = Modifier.size(30.dp).testTag("action_greetings")
                ) {
                    Icon(
                        imageVector = Icons.Default.CardGiftcard,
                        contentDescription = "Telugu Greetings & Wishes",
                        tint = if (activePanel == KeyboardPanel.GREETINGS_WISHES) palette.accent else palette.keySecondaryText,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // 7. Fancy Fonts Converter
                IconButton(
                    onClick = {
                        onTogglePanel(
                            if (activePanel == KeyboardPanel.FANCY_FONTS) KeyboardPanel.NONE else KeyboardPanel.FANCY_FONTS
                        )
                    },
                    modifier = Modifier.size(30.dp).testTag("action_fancy_fonts")
                ) {
                    Icon(
                        imageVector = Icons.Default.TextFields,
                        contentDescription = "Fancy Font Styles",
                        tint = if (activePanel == KeyboardPanel.FANCY_FONTS) palette.accent else palette.keySecondaryText,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // 8. Emojis (Telugu Categorized)
                IconButton(
                    onClick = {
                        onTogglePanel(
                            if (activePanel == KeyboardPanel.EMOJI_PICKER) KeyboardPanel.NONE else KeyboardPanel.EMOJI_PICKER
                        )
                    },
                    modifier = Modifier.size(30.dp).testTag("action_emojis")
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEmotions,
                        contentDescription = "Telugu Emojis",
                        tint = if (activePanel == KeyboardPanel.EMOJI_PICKER) palette.accent else palette.keySecondaryText,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // 9. App Search & Quick Launch
                IconButton(
                    onClick = {
                        onTogglePanel(
                            if (activePanel == KeyboardPanel.APP_SEARCH) KeyboardPanel.NONE else KeyboardPanel.APP_SEARCH
                        )
                    },
                    modifier = Modifier.size(30.dp).testTag("action_app_search")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "App Search & Quick Launch",
                        tint = if (activePanel == KeyboardPanel.APP_SEARCH) palette.accent else palette.keySecondaryText,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // 10. Smart Clipboard
                IconButton(
                    onClick = {
                        onTogglePanel(
                            if (activePanel == KeyboardPanel.CLIPBOARD) KeyboardPanel.NONE else KeyboardPanel.CLIPBOARD
                        )
                    },
                    modifier = Modifier.size(30.dp).testTag("action_clipboard")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentPaste,
                        contentDescription = "Smart Clipboard Snippets",
                        tint = if (activePanel == KeyboardPanel.CLIPBOARD) palette.accent else palette.keySecondaryText,
                        modifier = Modifier.size(17.dp)
                    )
                }


                // 11. Text Editing D-Pad
                IconButton(
                    onClick = {
                        onTogglePanel(
                            if (activePanel == KeyboardPanel.DPAD) KeyboardPanel.NONE else KeyboardPanel.DPAD
                        )
                    },
                    modifier = Modifier.size(30.dp).testTag("action_dpad")
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenWith,
                        contentDescription = "Text Editing D-Pad",
                        tint = if (activePanel == KeyboardPanel.DPAD) palette.accent else palette.keySecondaryText,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }

            // Quick 1-Tap Telugu / English Language Switch Pill (Iconic Desh Keyboard Feature)
            val isTeluguActive = layoutMode == KeyboardLayoutMode.TENGLISH || layoutMode == KeyboardLayoutMode.NATIVE_TELUGU
            Surface(
                onClick = onSwitchLayout,
                shape = RoundedCornerShape(12.dp),
                color = if (isTeluguActive) palette.accent.copy(alpha = 0.15f) else palette.specialKeyBackground,
                modifier = Modifier
                    .height(26.dp)
                    .padding(start = 4.dp, end = 2.dp)
                    .testTag("action_lang_switch")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = "Language",
                        tint = if (isTeluguActive) palette.accent else palette.keySecondaryText,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = when (layoutMode) {
                            KeyboardLayoutMode.TENGLISH -> "తెలుగు"
                            KeyboardLayoutMode.NATIVE_TELUGU -> "అక్షరాలు"
                            KeyboardLayoutMode.ENGLISH -> "English"
                            KeyboardLayoutMode.SYMBOLS, KeyboardLayoutMode.MORE_SYMBOLS -> "?123"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isTeluguActive) palette.accent else palette.keyText
                    )
                }
            }
        }

        // Suggestions Row (3-Tier)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(horizontal = 4.dp, vertical = 2.dp)
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (teluguSuggestion.isNotEmpty()) {
                // Tier 1: Primary Telugu Transliterated Word
                SuggestionChip(
                    text = teluguSuggestion,
                    label = "Telugu",
                    isPrimary = true,
                    palette = palette,
                    onClick = { onSelectSuggestion(teluguSuggestion) }
                )
            }

            if (englishSuggestion.isNotEmpty()) {
                VerticalDivider(
                    modifier = Modifier.height(20.dp).padding(horizontal = 4.dp),
                    color = palette.border
                )

                // Tier 2: Literal English String
                SuggestionChip(
                    text = englishSuggestion,
                    label = "English",
                    isPrimary = false,
                    palette = palette,
                    onClick = { onSelectSuggestion(englishSuggestion) }
                )
            }

            if (alternateSuggestion.isNotEmpty()) {
                VerticalDivider(
                    modifier = Modifier.height(20.dp).padding(horizontal = 4.dp),
                    color = palette.border
                )

                // Tier 3: Next word or alternate phonetic spelling
                SuggestionChip(
                    text = alternateSuggestion,
                    label = if (currentWord.isEmpty()) "Next" else "Alternate",
                    isPrimary = false,
                    palette = palette,
                    onClick = { onSelectSuggestion(alternateSuggestion) }
                )
            }

            if (teluguSuggestion.isEmpty() && englishSuggestion.isEmpty()) {
                // Default quick starter words
                listOf("నమస్కారం", "బాగున్నావా", "రేపు", "ధన్యవాదాలు").forEach { quickWord ->
                    SuggestionChip(
                        text = quickWord,
                        label = null,
                        isPrimary = false,
                        palette = palette,
                        onClick = { onSelectSuggestion(quickWord) }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }
        }
    }
}

@Composable
private fun SuggestionChip(
    text: String,
    label: String?,
    isPrimary: Boolean,
    palette: KeyboardPalette,
    onClick: () -> Unit
) {
    val view = LocalView.current
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isPrimary) palette.accent.copy(alpha = 0.2f) else palette.surface)
            .clickable {
                try {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                } catch (_: Exception) {}
                onClick()
            }
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .testTag("suggestion_$text"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = text,
                fontSize = if (isPrimary) 15.sp else 13.sp,
                fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.Medium,
                color = if (isPrimary) palette.accent else palette.suggestionText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (label != null) {
                Text(
                    text = label,
                    fontSize = 9.sp,
                    color = palette.keySecondaryText
                )
            }
        }
    }
}
