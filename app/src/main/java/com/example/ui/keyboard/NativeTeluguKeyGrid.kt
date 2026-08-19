package com.example.ui.keyboard

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.data.KeyboardPreferences

@Composable
fun NativeTeluguKeyGrid(
    modifier: Modifier = Modifier,
    palette: KeyboardPalette,
    preferences: KeyboardPreferences,
    isShifted: Boolean,
    onKeyPress: (String) -> Unit,
    onOpenGuninthaluPopup: (String) -> Unit,
    onShiftToggle: () -> Unit,
    onBackspace: () -> Unit,
    onBackspaceSwipeDelete: () -> Unit,
    onSpacePress: () -> Unit,
    onSpaceCursorDrag: (Float) -> Unit,
    onEnterPress: () -> Unit,
    onSwitchToSymbols: () -> Unit,
    onSwitchLayout: () -> Unit
) {
    var lastSelectedConsonant by remember { mutableStateOf("క") }

    // Normal Telugu Keymap: Row 1 (క గ చ జ ట డ త ద న ప బ మ)
    val row1Normal = listOf("క", "గ", "చ", "జ", "ట", "డ", "త", "ద", "న", "ప", "బ", "మ")
    val row1Shifted = listOf("ఖ", "ఘ", "ఛ", "ఝ", "ఠ", "ఢ", "థ", "ధ", "ణ", "ఫ", "భ", "ఙ")

    // Row 2 (య ర ల వ శ ష స హ ళ క్ష ఱ)
    val row2Normal = listOf("య", "ర", "ల", "వ", "శ", "ష", "స", "హ", "ళ", "క్ష", "ఱ")
    val row2Shifted = listOf("అ", "ఆ", "ఇ", "ఈ", "ఉ", "ఊ", "ఋ", "ఎ", "ఏ", "ఐ", "ఒ")

    // Row 3 (Matras & Dependent vowels: ా ి ీ ు ూ ె ే ై ొ ో ం ్)
    val row3Normal = listOf("ా", "ి", "ీ", "ు", "ూ", "ె", "ే", "ై", "ొ", "ో", "ం", "్")
    val row3Shifted = listOf("ఓ", "ఔ", "అం", "అః", "ృ", "ౄ", "ౌ", "ః", "౦", "౧", "౨", "౩")

    val activeRow1 = if (isShifted) row1Shifted else row1Normal
    val activeRow2 = if (isShifted) row2Shifted else row2Normal
    val activeRow3 = if (isShifted) row3Shifted else row3Normal

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            activeRow1.forEach { char ->
                KeyComponent(
                    modifier = Modifier.weight(1f),
                    primaryText = char,
                    palette = palette,
                    preferences = preferences,
                    onClick = {
                        lastSelectedConsonant = char
                        onKeyPress(char)
                    },
                    onLongClick = {
                        lastSelectedConsonant = char
                        onOpenGuninthaluPopup(char)
                    }
                )
            }
        }

        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            activeRow2.forEach { char ->
                KeyComponent(
                    modifier = Modifier.weight(1f),
                    primaryText = char,
                    palette = palette,
                    preferences = preferences,
                    onClick = {
                        if (!isShifted) lastSelectedConsonant = char
                        onKeyPress(char)
                    },
                    onLongClick = {
                        lastSelectedConsonant = char
                        onOpenGuninthaluPopup(char)
                    }
                )
            }
        }

        // Row 3 (Shift, Matras, Backspace)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Shift / Vowel layer toggle with bold shift icon and layer hint
            KeyComponent(
                modifier = Modifier.weight(1.3f),
                primaryText = if (isShifted) "⇪" else "⇧",
                secondaryText = if (isShifted) "అ/ఖ" else "క/హ",
                isSpecial = true,
                isAccent = isShifted,
                palette = palette,
                preferences = preferences,
                onClick = onShiftToggle
            )

            activeRow3.forEach { char ->
                KeyComponent(
                    modifier = Modifier.weight(1f),
                    primaryText = char,
                    palette = palette,
                    preferences = preferences,
                    onClick = { onKeyPress(char) }
                )
            }

            // Backspace with swipe-to-delete
            var dragDistance by remember { mutableFloatStateOf(0f) }
            Box(
                modifier = Modifier
                    .weight(1.3f)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                if (dragDistance < -60f) {
                                    onBackspaceSwipeDelete()
                                }
                                dragDistance = 0f
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                dragDistance += dragAmount
                            }
                        )
                    }
            ) {
                KeyComponent(
                    modifier = Modifier.fillMaxWidth(),
                    primaryText = "⌫",
                    isSpecial = true,
                    palette = palette,
                    preferences = preferences,
                    onClick = onBackspace,
                    onLongClick = onBackspaceSwipeDelete
                )
            }
        }

        // Row 4 (Symbols, Language switch, Spacebar, Guninthalu trigger, Enter)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            KeyComponent(
                modifier = Modifier.weight(1.2f),
                primaryText = "?౧౨౩",
                isSpecial = true,
                palette = palette,
                preferences = preferences,
                onClick = onSwitchToSymbols
            )

            KeyComponent(
                modifier = Modifier.weight(1.1f),
                primaryText = "తె/EN",
                isSpecial = true,
                palette = palette,
                preferences = preferences,
                onClick = onSwitchLayout
            )

            // Dynamic Guninthalu & Vottu popover quick key (Uses lastSelectedConsonant)
            KeyComponent(
                modifier = Modifier.weight(1.3f),
                primaryText = "గుణింతాలు",
                isSpecial = true,
                palette = palette,
                preferences = preferences,
                onClick = { onOpenGuninthaluPopup(lastSelectedConsonant) }
            )

            // Spacebar
            Box(
                modifier = Modifier
                    .weight(3.3f)
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
                    primaryText = "స్పేస్ (Space)",
                    palette = palette,
                    preferences = preferences,
                    onClick = onSpacePress
                )
            }

            KeyComponent(
                modifier = Modifier.weight(0.9f),
                primaryText = "।",
                palette = palette,
                preferences = preferences,
                onClick = { onKeyPress("।") }
            )

            KeyComponent(
                modifier = Modifier.weight(1.3f),
                primaryText = "↵",
                isAccent = true,
                palette = palette,
                preferences = preferences,
                onClick = onEnterPress
            )
        }
    }
}
