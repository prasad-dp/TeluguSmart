package com.example.ui.keyboard

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
fun TenglishKeyGrid(
    modifier: Modifier = Modifier,
    palette: KeyboardPalette,
    preferences: KeyboardPreferences,
    isShifted: Boolean,
    onKeyPress: (String) -> Unit,
    onShiftToggle: () -> Unit,
    onBackspace: () -> Unit,
    onBackspaceSwipeDelete: () -> Unit,
    onSpacePress: () -> Unit,
    onSpaceCursorDrag: (Float) -> Unit,
    onEnterPress: () -> Unit,
    onSwitchToSymbols: () -> Unit,
    onSwitchLayout: () -> Unit
) {
    val row1 = listOf(
        "q" to "1", "w" to "2", "e" to "3", "r" to "4", "t" to "5",
        "y" to "6", "u" to "7", "i" to "8", "o" to "9", "p" to "0"
    )
    val row2 = listOf(
        "a" to "@", "s" to "#", "d" to "$", "f" to "%", "g" to "&",
        "h" to "*", "j" to "-", "k" to "+", "l" to "="
    )
    val row3 = listOf(
        "z" to "_", "x" to "/", "c" to ":", "v" to ";",
        "b" to "(", "n" to ")", "m" to "?"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            row1.forEach { (char, sec) ->
                val text = if (isShifted) char.uppercase() else char
                KeyComponent(
                    modifier = Modifier.weight(1f),
                    primaryText = text,
                    secondaryText = sec,
                    palette = palette,
                    preferences = preferences,
                    onClick = { onKeyPress(text) },
                    onLongClick = { onKeyPress(sec) }
                )
            }
        }

        // Row 2
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            row2.forEach { (char, sec) ->
                val text = if (isShifted) char.uppercase() else char
                KeyComponent(
                    modifier = Modifier.weight(1f),
                    primaryText = text,
                    secondaryText = sec,
                    palette = palette,
                    preferences = preferences,
                    onClick = { onKeyPress(text) },
                    onLongClick = { onKeyPress(sec) }
                )
            }
        }

        // Row 3 (Shift, characters, Backspace with swipe gesture)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Shift Key
            KeyComponent(
                modifier = Modifier.weight(1.3f),
                primaryText = if (isShifted) "⇪" else "⇧",
                isSpecial = true,
                isAccent = isShifted,
                palette = palette,
                preferences = preferences,
                onClick = onShiftToggle
            )

            row3.forEach { (char, sec) ->
                val text = if (isShifted) char.uppercase() else char
                KeyComponent(
                    modifier = Modifier.weight(1f),
                    primaryText = text,
                    secondaryText = sec,
                    palette = palette,
                    preferences = preferences,
                    onClick = { onKeyPress(text) },
                    onLongClick = { onKeyPress(sec) }
                )
            }

            // Backspace with swipe-to-delete gesture
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

        // Row 4 (Symbols, Language, Telugu virama, Spacebar with cursor drag gesture, Period, Enter)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ?123 Mode
            KeyComponent(
                modifier = Modifier.weight(1.2f),
                primaryText = "?123",
                isSpecial = true,
                palette = palette,
                preferences = preferences,
                onClick = onSwitchToSymbols
            )

            // Switch layout (తె / EN)
            KeyComponent(
                modifier = Modifier.weight(1.1f),
                primaryText = "తె/EN",
                isSpecial = true,
                palette = palette,
                preferences = preferences,
                onClick = onSwitchLayout
            )

            // Virama / Comma
            KeyComponent(
                modifier = Modifier.weight(0.9f),
                primaryText = ",",
                secondaryText = "్",
                palette = palette,
                preferences = preferences,
                onClick = { onKeyPress(",") },
                onLongClick = { onKeyPress("్") }
            )

            // Spacebar with continuous cursor drag control
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
                    primaryText = "Telugu (తెలుగు)",
                    palette = palette,
                    preferences = preferences,
                    onClick = onSpacePress
                )
            }

            // Period / Telugu Danda
            KeyComponent(
                modifier = Modifier.weight(0.9f),
                primaryText = ".",
                secondaryText = "।",
                palette = palette,
                preferences = preferences,
                onClick = { onKeyPress(".") },
                onLongClick = { onKeyPress("।") }
            )

            // Enter Key
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
