package com.example.ui.keyboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.VerticalAlignBottom
import androidx.compose.material.icons.filled.VerticalAlignTop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TextEditingDpad(
    modifier: Modifier = Modifier,
    palette: KeyboardPalette,
    onMoveCursorLeft: () -> Unit,
    onMoveCursorRight: () -> Unit,
    onMoveCursorUp: () -> Unit,
    onMoveCursorDown: () -> Unit,
    onMoveToStart: () -> Unit,
    onMoveToEnd: () -> Unit,
    onSelectAll: () -> Unit,
    onCut: () -> Unit,
    onCopy: () -> Unit,
    onPaste: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(palette.surface)
            .padding(12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Text Editing D-Pad (కర్సర్ నియంత్రణ)",
                color = palette.keyText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(28.dp).testTag("close_dpad")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close D-Pad",
                    tint = palette.keySecondaryText
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Action side buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DpadActionButton(icon = Icons.Default.SelectAll, label = "Select All", palette = palette, onClick = onSelectAll)
                DpadActionButton(icon = Icons.Default.ContentCut, label = "Cut", palette = palette, onClick = onCut)
                DpadActionButton(icon = Icons.Default.ContentCopy, label = "Copy", palette = palette, onClick = onCopy)
                DpadActionButton(icon = Icons.Default.ContentPaste, label = "Paste", palette = palette, onClick = onPaste)
            }

            // D-Pad Cross
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Up
                DpadArrowButton(icon = Icons.Default.ArrowUpward, desc = "Up", palette = palette, onClick = onMoveCursorUp)

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left
                    DpadArrowButton(icon = Icons.AutoMirrored.Filled.ArrowBack, desc = "Left", palette = palette, onClick = onMoveCursorLeft)

                    // Center Jump start/end
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(palette.accent)
                            .clickable(onClick = onSelectAll),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "SEL",
                            color = palette.accentText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Right
                    DpadArrowButton(icon = Icons.AutoMirrored.Filled.ArrowForward, desc = "Right", palette = palette, onClick = onMoveCursorRight)
                }

                // Down
                DpadArrowButton(icon = Icons.Default.ArrowDownward, desc = "Down", palette = palette, onClick = onMoveCursorDown)
            }

            // Jump buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DpadActionButton(icon = Icons.Default.VerticalAlignTop, label = "Start", palette = palette, onClick = onMoveToStart)
                DpadActionButton(icon = Icons.Default.VerticalAlignBottom, label = "End", palette = palette, onClick = onMoveToEnd)
            }
        }
    }
}

@Composable
private fun DpadArrowButton(
    icon: ImageVector,
    desc: String,
    palette: KeyboardPalette,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(palette.keyBackground)
            .border(1.dp, palette.border, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .testTag("dpad_arrow_$desc"),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = desc,
            tint = palette.accent,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun DpadActionButton(
    icon: ImageVector,
    label: String,
    palette: KeyboardPalette,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(palette.keyBackground)
            .border(0.5.dp, palette.border, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("dpad_action_$label"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = palette.keyText,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                color = palette.keyText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
