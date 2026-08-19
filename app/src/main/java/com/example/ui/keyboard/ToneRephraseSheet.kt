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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.TeluguToneType
import com.example.engine.ToneRephraser

@Composable
fun ToneRephraseSheet(
    modifier: Modifier = Modifier,
    palette: KeyboardPalette,
    currentText: String,
    onApplyRephrase: (String) -> Unit,
    onClose: () -> Unit
) {
    var selectedTone by remember { mutableStateOf(TeluguToneType.CASUAL) }
    val rephrasedText = remember(currentText, selectedTone) {
        ToneRephraser.rephraseText(
            if (currentText.isBlank()) "నమస్కారం ఎలా ఉన్నారు" else currentText,
            selectedTone
        )
    }

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
                    text = "On-Device GenAI Assistant",
                    color = palette.keyText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier.size(28.dp).testTag("close_tone_sheet")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = palette.keySecondaryText
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tone Selector Tabs
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(TeluguToneType.entries) { tone ->
                val isSelected = tone == selectedTone
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) palette.accent else palette.keyBackground)
                        .border(
                            0.5.dp,
                            if (isSelected) palette.accent else palette.border,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { selectedTone = tone }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("tone_opt_${tone.name}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tone.label,
                        color = if (isSelected) palette.accentText else palette.keyText,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Rephrased Preview Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(palette.keyBackground)
                .border(1.dp, palette.border, RoundedCornerShape(10.dp))
                .padding(10.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = selectedTone.teluguLabel,
                    fontSize = 10.sp,
                    color = palette.accent,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = rephrasedText,
                    fontSize = 14.sp,
                    color = palette.keyText,
                    fontWeight = FontWeight.Normal
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Apply Button
        Button(
            onClick = {
                onApplyRephrase(rephrasedText)
                onClose()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = palette.accent,
                contentColor = palette.accentText
            ),
            modifier = Modifier.fillMaxWidth().height(40.dp).testTag("apply_rephrase_btn")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Apply",
                    modifier = Modifier.size(16.dp)
                )
                Text("Insert Rephrased Text", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
