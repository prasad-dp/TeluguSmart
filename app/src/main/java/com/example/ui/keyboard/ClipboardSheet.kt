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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Badge
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ClipboardSnippet

@Composable
fun ClipboardSheet(
    modifier: Modifier = Modifier,
    palette: KeyboardPalette,
    snippets: List<ClipboardSnippet>,
    onSelectSnippet: (String) -> Unit,
    onTogglePin: (ClipboardSnippet) -> Unit,
    onDeleteSnippet: (ClipboardSnippet) -> Unit,
    onClearUnpinned: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
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
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Smart Clipboard (క్లిప్‌బోర్డ్)",
                    color = palette.keyText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                if (snippets.isNotEmpty()) {
                    OutlinedButton(
                        onClick = onClearUnpinned,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = palette.accent
                        ),
                        modifier = Modifier.height(26.dp).testTag("clear_unpinned_clipboard")
                    ) {
                        Text("Clear Recent", fontSize = 10.sp)
                    }
                }
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier.size(28.dp).testTag("close_clipboard_sheet")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Clipboard",
                    tint = palette.keySecondaryText
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (snippets.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No copied text yet. Copy text or OTP to see it here.",
                    color = palette.keySecondaryText,
                    fontSize = 12.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(snippets, key = { it.id }) { snippet ->
                    ClipboardSnippetItem(
                        snippet = snippet,
                        palette = palette,
                        onSelect = { onSelectSnippet(snippet.content) },
                        onTogglePin = { onTogglePin(snippet) },
                        onDelete = { onDeleteSnippet(snippet) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ClipboardSnippetItem(
    snippet: ClipboardSnippet,
    palette: KeyboardPalette,
    onSelect: () -> Unit,
    onTogglePin: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(palette.keyBackground)
            .border(
                if (snippet.isPinned) 1.dp else 0.5.dp,
                if (snippet.isPinned) palette.accent else palette.border,
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onSelect)
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .testTag("clipboard_item_${snippet.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (snippet.detectedType == "OTP") {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(palette.accent.copy(alpha = 0.2f))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "OTP Detected",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.accent
                            )
                        }
                    } else if (snippet.detectedType == "PHONE") {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(palette.specialKeyBackground)
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Phone",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = palette.specialKeyText
                            )
                        }
                    }
                    Text(
                        text = snippet.content,
                        color = palette.keyText,
                        fontSize = 13.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onTogglePin,
                    modifier = Modifier.size(28.dp).testTag("pin_btn_${snippet.id}")
                ) {
                    Icon(
                        imageVector = if (snippet.isPinned) Icons.Default.PushPin else Icons.Default.Pin,
                        contentDescription = "Pin/Unpin",
                        tint = if (snippet.isPinned) palette.accent else palette.keySecondaryText,
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp).testTag("delete_btn_${snippet.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = palette.keySecondaryText,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
