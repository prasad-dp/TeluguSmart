package com.example.ui.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KeyboardPreferences

@Composable
fun NativeTeluguKeyGrid(
    modifier: Modifier = Modifier,
    palette: KeyboardPalette,
    preferences: KeyboardPreferences,
    isShifted: Boolean = false,
    onKeyPress: (String) -> Unit,
    onReplaceLastCharAndCommit: (String) -> Unit = { char ->
        onKeyPress(char)
    },
    onOpenGuninthaluPopup: (String) -> Unit = {},
    onShiftToggle: () -> Unit = {},
    onBackspace: () -> Unit,
    onBackspaceSwipeDelete: () -> Unit,
    onSpacePress: () -> Unit,
    onSpaceLongPress: (() -> Unit)? = null,
    onSpaceCursorDrag: (Float) -> Unit,
    onEnterPress: () -> Unit,
    onSwitchToSymbols: () -> Unit,
    onSwitchLayout: () -> Unit,
    onDismissKeyboard: (() -> Unit)? = null
) {
    // Tracks active consonant for real-time dynamic Guninthalu transformation
    var activeConsonant by remember { mutableStateOf<String?>(null) }

    // Helper to generate the exact 17 Guninthalu forms for any Telugu consonant
    fun getGuninthalu(c: String): List<String> {
        return listOf(
            c,            // 0: Root Consonant (క)
            c + "ా",      // 1: దీర్ఘం (కా)
            c + "ి",      // 2: గుడి (కి)
            c + "ీ",      // 3: గుడిదీర్ఘం (కీ)
            c + "ు",      // 4: కొమ్ము (కు)
            c + "ూ",      // 5: కొమ్ముదీర్ఘం (కూ)
            c + "ృ",      // 6: వట్రుసుడి (కృ)
            c + "ౄ",      // 7: వట్రుసుడి దీర్ఘం (కౄ)
            c + "ె",      // 8: ఎత్వం (కె)
            c + "ే",      // 9: ఏత్వం (కే)
            c + "ై",      // 10: ఐత్వం (కై)
            c + "ొ",      // 11: ఒత్వం (కొ)
            c + "ో",      // 12: ఓత్వం (కో)
            c + "ౌ",      // 13: ఔత్వం (కౌ)
            c + "ం",      // 14: సున్నా / అనుస్వార (కం)
            c + "ః",      // 15: విసర్గ (కః)
            c + "్"       // 16: పొల్లు / విరామ (క్)
        )
    }

    // Base Vowels when no consonant is active
    val baseVowelsRow1 = listOf("అ", "ఆ", "ఇ", "ఈ", "ఉ", "ఊ", "ఋ", "ౠ", "ఎ", "ఏ")
    val baseVowelsRow2 = listOf("ఐ", "ఒ", "ఓ", "ఔ", "ం", "ః")
    val baseVirama = "్"

    // Consonants layout across rows
    val consonantsRow2 = listOf("క", "ఖ", "గ", "ఘ")
    val consonantsRow3 = listOf("చ", "ఛ", "జ", "ఝ", "ట", "ఠ", "డ", "ఢ", "ణ")
    val consonantsRow4 = listOf("త", "థ", "ద", "ధ", "న", "ప", "ఫ", "బ", "భ", "మ")
    val consonantsRow5 = listOf("య", "ర", "ల", "వ", "ళ", "శ", "ష", "స", "హ")

    // Dynamic Guninthalu calculation
    val guninthaluList = activeConsonant?.let { getGuninthalu(it) }

    val row1Keys = guninthaluList?.subList(0, 10) ?: baseVowelsRow1
    val row2VowelOrGuninthaluKeys = guninthaluList?.subList(10, 16) ?: baseVowelsRow2
    val row3ViramaOrGuninthaluKey = guninthaluList?.get(16) ?: baseVirama

    val keyHeight = 38.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 1.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // ==================== ROW 1 (10 Keys) ====================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            row1Keys.forEach { char ->
                KeyComponent(
                    modifier = Modifier.weight(1f),
                    primaryText = char,
                    isSpecial = true,
                    keyHeight = keyHeight,
                    palette = palette,
                    preferences = preferences,
                    onClick = {
                        if (activeConsonant != null) {
                            onReplaceLastCharAndCommit(char)
                            activeConsonant = null
                        } else {
                            onKeyPress(char)
                        }
                    },
                    onLongClick = {
                        if (activeConsonant != null) {
                            onOpenGuninthaluPopup(activeConsonant!!)
                        }
                    }
                )
            }
        }

        // ==================== ROW 2 (10 Keys) ====================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            row2VowelOrGuninthaluKeys.forEach { char ->
                KeyComponent(
                    modifier = Modifier.weight(1f),
                    primaryText = char,
                    isSpecial = true,
                    keyHeight = keyHeight,
                    palette = palette,
                    preferences = preferences,
                    onClick = {
                        if (activeConsonant != null) {
                            onReplaceLastCharAndCommit(char)
                            activeConsonant = null
                        } else {
                            onKeyPress(char)
                        }
                    }
                )
            }

            consonantsRow2.forEach { char ->
                KeyComponent(
                    modifier = Modifier.weight(1f),
                    primaryText = char,
                    isSpecial = false,
                    keyHeight = keyHeight,
                    palette = palette,
                    preferences = preferences,
                    onClick = {
                        activeConsonant = char
                        onKeyPress(char)
                    },
                    onLongClick = {
                        activeConsonant = char
                        onOpenGuninthaluPopup(char)
                    }
                )
            }
        }

        // ==================== ROW 3 (10 Keys) ====================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            KeyComponent(
                modifier = Modifier.weight(1f),
                primaryText = row3ViramaOrGuninthaluKey,
                isSpecial = true,
                keyHeight = keyHeight,
                palette = palette,
                preferences = preferences,
                onClick = {
                    if (activeConsonant != null) {
                        onReplaceLastCharAndCommit(row3ViramaOrGuninthaluKey)
                        activeConsonant = null
                    } else {
                        onKeyPress(baseVirama)
                    }
                }
            )

            consonantsRow3.forEach { char ->
                KeyComponent(
                    modifier = Modifier.weight(1f),
                    primaryText = char,
                    isSpecial = false,
                    keyHeight = keyHeight,
                    palette = palette,
                    preferences = preferences,
                    onClick = {
                        activeConsonant = char
                        onKeyPress(char)
                    },
                    onLongClick = {
                        activeConsonant = char
                        onOpenGuninthaluPopup(char)
                    }
                )
            }
        }

        // ==================== ROW 4 (10 Keys) ====================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            consonantsRow4.forEach { char ->
                KeyComponent(
                    modifier = Modifier.weight(1f),
                    primaryText = char,
                    isSpecial = false,
                    keyHeight = keyHeight,
                    palette = palette,
                    preferences = preferences,
                    onClick = {
                        activeConsonant = char
                        onKeyPress(char)
                    },
                    onLongClick = {
                        activeConsonant = char
                        onOpenGuninthaluPopup(char)
                    }
                )
            }
        }

        // ==================== ROW 5 (10 Keys: 9 Consonants + Backspace) ====================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            consonantsRow5.forEach { char ->
                KeyComponent(
                    modifier = Modifier.weight(1f),
                    primaryText = char,
                    isSpecial = false,
                    keyHeight = keyHeight,
                    palette = palette,
                    preferences = preferences,
                    onClick = {
                        activeConsonant = char
                        onKeyPress(char)
                    },
                    onLongClick = {
                        activeConsonant = char
                        onOpenGuninthaluPopup(char)
                    }
                )
            }

            KeyComponent(
                modifier = Modifier.weight(1f),
                primaryText = "⌫",
                isSpecial = true,
                keyHeight = keyHeight,
                palette = palette,
                preferences = preferences,
                onClick = {
                    activeConsonant = null
                    onBackspace()
                },
                onLongClick = {
                    activeConsonant = null
                    onBackspaceSwipeDelete()
                }
            )
        }

        // ==================== ROW 6 (Bottom Action & Spacebar Row) ====================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ?123 Key (Symbols switch)
            KeyComponent(
                modifier = Modifier.weight(1.25f),
                primaryText = "?123",
                isSpecial = true,
                keyHeight = keyHeight,
                palette = palette,
                preferences = preferences,
                onClick = {
                    activeConsonant = null
                    onSwitchToSymbols()
                }
            )

            // ;,: Punctuation Key
            KeyComponent(
                modifier = Modifier.weight(0.9f),
                primaryText = "; , :",
                isSpecial = true,
                keyHeight = keyHeight,
                palette = palette,
                preferences = preferences,
                onClick = {
                    activeConsonant = null
                    onKeyPress(",")
                },
                onLongClick = {
                    activeConsonant = null
                    onKeyPress(";")
                }
            )

            // AB / CD Layout switcher (Switch to English / Tenglish)
            KeyComponent(
                modifier = Modifier.weight(0.9f),
                primaryText = "AB\nCD",
                isSpecial = false,
                keyHeight = keyHeight,
                palette = palette,
                preferences = preferences,
                onClick = {
                    activeConsonant = null
                    onSwitchLayout()
                }
            )

            // Spacebar ("Telugu Smart Keyboard")
            Box(
                modifier = Modifier
                    .weight(3.6f)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onHorizontalDrag = { _, dragAmount ->
                                onSpaceCursorDrag(dragAmount)
                            }
                        )
                    }
            ) {
                KeyComponent(
                    modifier = Modifier.fillMaxWidth(),
                    primaryText = "Telugu Smart Keyboard",
                    isSpecial = false,
                    keyHeight = keyHeight,
                    palette = palette,
                    preferences = preferences,
                    onClick = {
                        activeConsonant = null
                        onSpacePress()
                    },
                    onLongClick = {
                        activeConsonant = null
                        onSpaceLongPress?.invoke()
                    }
                )
            }

            // Period . Key
            KeyComponent(
                modifier = Modifier.weight(0.85f),
                primaryText = ".",
                isSpecial = true,
                keyHeight = keyHeight,
                palette = palette,
                preferences = preferences,
                onClick = {
                    activeConsonant = null
                    onKeyPress(".")
                }
            )

            // Checkmark ✓ / Enter Key
            KeyComponent(
                modifier = Modifier.weight(1.25f),
                primaryText = "",
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = "Enter",
                        modifier = Modifier.size(20.dp),
                        tint = palette.specialKeyText
                    )
                },
                isSpecial = true,
                keyHeight = keyHeight,
                palette = palette,
                preferences = preferences,
                onClick = {
                    activeConsonant = null
                    onEnterPress()
                }
            )
        }

        // ==================== ROW 7 (Bottom Navigation Bar) ====================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 1.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onDismissKeyboard?.invoke() },
                modifier = Modifier
                    .size(32.dp)
                    .testTag("btn_dismiss_keyboard")
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = "Hide Keyboard",
                    tint = palette.keySecondaryText,
                    modifier = Modifier.size(22.dp)
                )
            }

            if (activeConsonant != null) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(palette.specialKeyBackground)
                        .clickable { activeConsonant = null }
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "అ ఆ (Vowels)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = palette.accent
                    )
                }
            } else {
                Spacer(modifier = Modifier.size(1.dp))
            }

            IconButton(
                onClick = onSwitchLayout,
                modifier = Modifier
                    .size(32.dp)
                    .testTag("btn_globe_switch")
            ) {
                Icon(
                    imageVector = Icons.Rounded.Language,
                    contentDescription = "Switch Keyboard Layout",
                    tint = palette.keySecondaryText,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
