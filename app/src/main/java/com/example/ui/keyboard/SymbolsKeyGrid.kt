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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.data.KeyboardPreferences

@Composable
fun SymbolsKeyGrid(
    modifier: Modifier = Modifier,
    palette: KeyboardPalette,
    preferences: KeyboardPreferences,
    isMoreSymbols: Boolean,
    onKeyPress: (String) -> Unit,
    onToggleMoreSymbols: () -> Unit,
    onBackspace: () -> Unit,
    onBackspaceSwipeDelete: () -> Unit,
    onSpacePress: () -> Unit,
    onSpaceLongPress: (() -> Unit)? = null,
    onSpaceCursorDrag: (Float) -> Unit,
    onEnterPress: () -> Unit,
    onSwitchToLetters: () -> Unit
) {
    val row1Std = listOf(
        "1" to "౧", "2" to "౨", "3" to "౩", "4" to "౪", "5" to "౫",
        "6" to "౬", "7" to "౭", "8" to "౮", "9" to "౯", "0" to "౦"
    )
    val row2Std = listOf("@", "#", "₹", "$", "%", "&", "-", "+", "(", ")")
    val row3Std = listOf("*", "\"", "'", ":", ";", "!", "?", "/", "\\")

    val row1More = listOf("~", "`", "|", "•", "√", "π", "÷", "×", "¶", "∆")
    val row2More = listOf("£", "€", "¥", "¢", "^", "°", "=", "{", "}", "\\")
    val row3More = listOf("%", "©", "®", "™", "✓", "[", "]", "<", ">")

    val activeRow1 = if (isMoreSymbols) row1More.map { it to null } else row1Std
    val activeRow2 = if (isMoreSymbols) row2More else row2Std
    val activeRow3 = if (isMoreSymbols) row3More else row3Std

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Row 1 (Numbers / Telugu Digits)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            activeRow1.forEach { (char, sec) ->
                KeyComponent(
                    modifier = Modifier.weight(1f),
                    primaryText = char,
                    secondaryText = sec,
                    palette = palette,
                    preferences = preferences,
                    onClick = { onKeyPress(char) },
                    onLongClick = sec?.let { { onKeyPress(it) } }
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
                    onClick = { onKeyPress(char) }
                )
            }
        }

        // Row 3 (1/2 toggle, symbols, Backspace)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            KeyComponent(
                modifier = Modifier.weight(1.3f),
                primaryText = if (isMoreSymbols) "1/2" else "=<%",
                isSpecial = true,
                palette = palette,
                preferences = preferences,
                onClick = onToggleMoreSymbols
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

        // Row 4
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            KeyComponent(
                modifier = Modifier.weight(1.4f),
                primaryText = "ABC",
                isSpecial = true,
                palette = palette,
                preferences = preferences,
                onClick = onSwitchToLetters
            )

            KeyComponent(
                modifier = Modifier.weight(0.9f),
                primaryText = ",",
                palette = palette,
                preferences = preferences,
                onClick = { onKeyPress(",") }
            )

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
                    primaryText = "స్పేస్ (Space)",
                    palette = palette,
                    preferences = preferences,
                    onClick = onSpacePress,
                    onLongClick = onSpaceLongPress
                )
            }

            KeyComponent(
                modifier = Modifier.weight(0.9f),
                primaryText = ".",
                palette = palette,
                preferences = preferences,
                onClick = { onKeyPress(".") }
            )

            KeyComponent(
                modifier = Modifier.weight(1.4f),
                primaryText = "↵",
                isAccent = true,
                palette = palette,
                preferences = preferences,
                onClick = onEnterPress
            )
        }
    }
}
